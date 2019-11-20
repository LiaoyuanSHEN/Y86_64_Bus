package y86_64.memory;

import y86_64.Memory;
import y86_64.TransportUtil;
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
    private final Memory memory;
    private final Map<Processor, Thread> processors = new HashMap<>();
    private boolean isRunning = false;

    public MemoryTcpServer(int port, Memory memory) throws IOException {
        this.memory = memory;
        serverControlSocket = new ServerSocket(CONTROL_PORT);
        serverDataSocket = new ServerSocket(port);
    }

    public void stop() {
        isRunning = false;
        processors.values().forEach(Thread::interrupt);
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                Processor processor = new Processor(serverControlSocket.accept(), serverDataSocket.accept());
                Thread thread = new Thread(processor);
                thread.start();
                processors.put(processor, thread);
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
        }
    }

    private class Processor implements Runnable {

        private final Socket controlSocket;
        private final Socket dataSocket;
        private final InputStream controlInputStream;
        private final OutputStream controlOutputStream;
        private final InputStream dataInputStream;
        private final OutputStream dataOutputStream;

        private Processor(Socket controlSocket, Socket dataSocket) throws IOException {
            this.controlSocket = controlSocket;
            this.dataSocket = dataSocket;
            controlInputStream = controlSocket.getInputStream();
            controlOutputStream = controlSocket.getOutputStream();
            dataInputStream = dataSocket.getInputStream();
            dataOutputStream = dataSocket.getOutputStream();
        }

        @Override
        public void run() {
            try {
                try {
                    memory.init(controlInputStream.read());
                } catch (MemoryException e) {
                    TransportUtil.writeLongToOutputStream(toExceptionCode(e), controlOutputStream);
                    controlOutputStream.flush();
                    // log error
                    return;
                }
                while (isRunning && controlSocket.isConnected() && dataSocket.isConnected()) {
                    try {
                        int controlCode = controlInputStream.read();
                        switch (controlCode) {
                            case READ_FLAG:
                                long value = memory.read(TransportUtil.readLongFromInputStream(controlInputStream));
                                TransportUtil.writeLongToOutputStream(value, dataOutputStream);
                                dataOutputStream.flush();
                                break;
                            case WRITE_FLAG:
                                memory.write(TransportUtil.readLongFromInputStream(controlInputStream), TransportUtil.readLongFromInputStream(dataInputStream));
                                TransportUtil.writeLongToOutputStream(NO_ERROR, controlOutputStream);
                                controlOutputStream.flush();
                                break;
                            default:
                                throw new IllegalArgumentException("Unrecognized controlCode: " + controlCode);
                        }
                    } catch (MemoryException e) {
                        TransportUtil.writeLongToOutputStream(toExceptionCode(e), controlOutputStream);
                    } finally {
                        controlOutputStream.flush();
                        dataOutputStream.flush();
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
                controlInputStream.close();
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
            try {
                controlOutputStream.close();
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
            try {
                controlSocket.close();
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
            try {
                dataInputStream.close();
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
            try {
                dataSocket.close();
            } catch (IOException e) {
                SneakyThrow.sneakyThrow(e);
            }
        }

    }

}
