package y86_64.cpu;

import y86_64.CPU;
import y86_64.bus.TcpServer;
import y86_64.bus.TransportUtil;
import y86_64.util.SneakyThrow;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

import static y86_64.bus.BusConst.*;

public class CpuTcpServer extends TcpServer<CPU> {

    private final ServerSocket controlServerSocket;
    private final ServerSocket dataServerSocket;
    private final List<CpuTcpServerSocketProcessor> processors = new LinkedList<>();
    private Thread serverThread = null;
    private boolean running = false;

    public CpuTcpServer(CPU cpu) throws IOException {
        super(cpu);
        controlServerSocket = new ServerSocket(CPU_CONTROL_PORT);
        dataServerSocket = new ServerSocket(CPU_DATA_PORT);
    }

    @Override
    public synchronized void run() {
        running = true;
        serverThread = new Thread(() -> {
            while (running) {
                try {
                    CpuTcpServerSocketProcessor processor = new CpuTcpServerSocketProcessor(
                            component,
                            this,
                            controlServerSocket.accept(),
                            dataServerSocket.accept());
                    processor.run();
                    processors.add(processor);
                } catch (InterruptedIOException e) {
                    // log interrupt
                    stop();
                } catch (IOException e) {
                    stop();
                    SneakyThrow.sneakyThrow(e);
                }
            }
        });
        serverThread.start();
    }

    @Override
    public void stop() {
        running = false;
        if (!serverThread.isInterrupted()) {
            serverThread.interrupt();
        }
        processors.forEach(CpuTcpServerSocketProcessor::close);
        TransportUtil.closeResourcesWithWrappedExceptions(controlServerSocket::close, dataServerSocket::close);
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
