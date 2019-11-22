package y86_64.memory;

import y86_64.Memory;
import y86_64.bus.TcpBus;
import y86_64.bus.TransportUtil;
import y86_64.exceptions.MemoryException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;

import static y86_64.memory.MemoryConst.*;

public class MemoryTcpServerSocketProcessor {

    private final Memory memory;
    private final MemoryTcpServer memoryTcpServer;
    private final TcpBus controlBus;
    private final TcpBus dataBus;
    private final TcpBus addressBus;
    private Thread thread = null;

    public MemoryTcpServerSocketProcessor(Memory memory, MemoryTcpServer memoryTcpServer, Socket controlSocket, Socket dataSocket, Socket addressSocket) throws IOException {
        this.memory = memory;
        this.memoryTcpServer = memoryTcpServer;
        controlBus = new TcpBus(controlSocket);
        dataBus = new TcpBus(dataSocket);
        addressBus = new TcpBus(addressSocket);
    }

    public synchronized void run() {
        if (thread != null) {
            throw new IllegalStateException("Memory server socket is already running.");
        }
        thread = new Thread(() -> {
            try {
                try {
                    memory.init(controlBus.readValue());
                } catch (MemoryException e) {
                    controlBus.writeValue(toExceptionCode(e));
                    // log error
                    return;
                }
                while (memoryTcpServer.isRunning() && controlBus.isConnected() && dataBus.isConnected() && addressBus.isConnected()) {
                    try {
                        long controlCode = controlBus.readValue();
                        switch ((int) controlCode) {
                            case READ_FLAG:
                                long value = memory.read(addressBus.readValue());
                                dataBus.writeValue(value);
                                break;
                            case WRITE_FLAG:
                                memory.write(addressBus.readValue(), dataBus.readValue());
                                break;
                            default:
                                throw new IllegalArgumentException("Unrecognized controlCode: " + controlCode);
                        }
                    } catch (MemoryException e) {
                        controlBus.writeValue(toExceptionCode(e));
                    }
                }
            } catch (InterruptedIOException e) {
                // log interrupt
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } finally {
                close();
            }
        });
        thread.start();
    }

    public void close() {
        if (!thread.isInterrupted()) {
            thread.interrupt();
        }
        TransportUtil.closeResourcesWithWrappedExceptions("close", controlBus, dataBus, addressBus);
    }
}
