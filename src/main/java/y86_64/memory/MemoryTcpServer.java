package y86_64.memory;

import y86_64.Memory;
import y86_64.bus.TcpBus;
import y86_64.bus.TcpServer;
import y86_64.exceptions.MemoryException;
import y86_64.util.SneakyThrow;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static y86_64.memory.MemoryConst.*;

public class MemoryTcpServer extends TcpServer<Memory> {

    private final ServerSocket controlServerSocket;
    private final ServerSocket dataServerSocket;
    private final ServerSocket addressServerSocket;
    private final Map<Processor, Thread> processors = new HashMap<>();
    private Thread serverThread = null;
    private boolean running = false;

    public MemoryTcpServer(Memory memory) throws IOException {
        super(memory);
        controlServerSocket = new ServerSocket(CONTROL_PORT);
        dataServerSocket = new ServerSocket(DATA_PORT);
        addressServerSocket = new ServerSocket(ADDRESS_PORT);
    }

    @Override
    public void start() {
        running = true;
        serverThread = new Thread(() -> {
            while (running) {
                try {
                    Processor processor = new Processor(controlServerSocket.accept(), dataServerSocket.accept(), addressServerSocket.accept());
                    Thread thread = new Thread(processor);
                    thread.start();
                    processors.put(processor, thread);
                } catch (InterruptedIOException e) {
                    // log interrupt
                } catch (IOException e) {
                    SneakyThrow.sneakyThrow(e);
                    break;
                } finally {
                    stop();
                }
            }
        });
        serverThread.start();
    }

    @Override
    public void stop() {
        running = false;
        serverThread.interrupt();
        try {
            controlServerSocket.close();
        } catch (IOException e) {
            SneakyThrow.sneakyThrow(e);
        }
        try {
            dataServerSocket.close();
        } catch (IOException e) {
            SneakyThrow.sneakyThrow(e);
        }
        try {
            addressServerSocket.close();
        } catch (IOException e) {
            SneakyThrow.sneakyThrow(e);
        }
        processors.values().forEach(Thread::interrupt);
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
                    component.init(controlBus.readValue());
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
                                long value = component.read(addressBus.readValue());
                                dataBus.writeValue(value);
                                break;
                            case WRITE_FLAG:
                                component.write(addressBus.readValue(), dataBus.readValue());
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
            Collection<Exception> exceptions = new LinkedList<>();
            exceptions.add(controlBus.close());
            exceptions.add(addressBus.close());
            exceptions.add(dataBus.close());
            exceptions = exceptions.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (exceptions.size() > 0) {
                throw new IllegalStateException(exceptions.toString());
            }
        }

    }

}
