package y86_64.memory;

import y86_64.Memory;
import y86_64.bus.TcpServer;
import y86_64.util.SneakyThrow;
import y86_64.util.TransportUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

import static y86_64.bus.BusConst.*;

public class MemoryTcpServer extends TcpServer<Memory> {

    private final ServerSocket controlServerSocket;
    private final ServerSocket dataServerSocket;
    private final ServerSocket addressServerSocket;
    private final List<MemoryTcpServerSocketProcessor> processors = new LinkedList<>();
    private Thread serverThread = null;
    private boolean running = false;

    public MemoryTcpServer(Memory memory) throws IOException {
        super(memory);
        controlServerSocket = new ServerSocket(MEMORY_CONTROL_PORT);
        dataServerSocket = new ServerSocket(MEMORY_DATA_PORT);
        addressServerSocket = new ServerSocket(MEMORY_ADDRESS_PORT);
    }

    @Override
    public synchronized void run() {
        running = true;
        serverThread = new Thread(() -> {
            while (running) {
                try {
                    MemoryTcpServerSocketProcessor processor = new MemoryTcpServerSocketProcessor(
                            component,
                            this,
                            controlServerSocket.accept(),
                            dataServerSocket.accept(),
                            addressServerSocket.accept());
                    processor.start();
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
        processors.forEach(MemoryTcpServerSocketProcessor::close);
        TransportUtil.closeResourcesWithWrappedExceptions(controlServerSocket::close, dataServerSocket::close, addressServerSocket::close);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

}
