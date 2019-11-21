package y86_64.memory;

import y86_64.Memory;
import y86_64.bus.TcpBus;
import y86_64.bus.TransportUtil;
import y86_64.exceptions.MemoryException;
import y86_64.util.SneakyThrow;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static y86_64.memory.MemoryConst.*;

public class MemoryTcpServer implements Runnable {

    private final ServerSocket serverControlSocket;
    private final ServerSocket serverDataSocket;
    private final ServerSocket serverAddressSocket;
    private final Memory memory;
    private final Map<Processor, Thread> processors = new HashMap<>();
    private boolean running = false;

    public MemoryTcpServer(Memory memory) throws IOException {
        this.memory = memory;
        serverControlSocket = new ServerSocket(CONTROL_PORT);
        serverDataSocket = new ServerSocket(DATA_PORT);
        serverAddressSocket = new ServerSocket(ADDRESS_PORT);
    }

    public void stop() {
        running = false;
        processors.values().forEach(Thread::interrupt);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                Processor processor = new Processor(serverControlSocket.accept(), serverDataSocket.accept(), serverAddressSocket.accept());
                Thread thread = new Thread(processor);
                thread.start();
                processors.put(processor, thread);
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
                break;
            }
        }
    }

    private class Processor implements Runnable {

        private final TcpBus controlBus;
        private final TcpBus dataBus;
        private final TcpBus addressBus;

        private Processor(Socket controlSocket, Socket dataSocket, Socket addressSocket) throws IOException {
            controlBus = new TcpBus(controlSocket);
            dataBus = new TcpBus(dataSocket);
            addressBus = new TcpBus(addressSocket);
        }

        @Override
        public void run() {
            try {
                try {
                    memory.init(controlBus.readValue());
                } catch (MemoryException e) {
                    controlBus.writeValue(toExceptionCode(e));
                    // log error
                    return;
                }
                while (running && controlBus.isConnected() && dataBus.isConnected() && addressBus.isConnected()) {
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
                closeResources();
            }
        }

        private void closeResources() {
            try {
                controlBus.close();
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
            try {
                addressBus.close();
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
            try {
                dataBus.close();
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
        }

    }

}
