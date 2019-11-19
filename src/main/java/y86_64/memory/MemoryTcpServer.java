package y86_64.memory;

import y86_64.Memory;
import y86_64.util.SneakyThrow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static y86_64.memory.MemoryConst.CONTROL_PORT;

public class MemoryTcpServer {

    private final ServerSocket serverControlSocket;
    private final ServerSocket serverDataSocket;
    private final Memory memory;
    private boolean isRunning = false;

    public MemoryTcpServer(int port, Memory memory) throws IOException {
        this.memory = memory;
        serverControlSocket = new ServerSocket(CONTROL_PORT);
        serverDataSocket = new ServerSocket(port);
        isRunning = true;
        new Thread(() -> {
            while (isRunning) {
                try {
                    new Thread(new Processor(serverControlSocket.accept(), serverDataSocket.accept())).start();
                } catch (IOException e) {
                    SneakyThrow.sneakyThrow(e);
                }
            }
        }).start();
    }

    public void stop() {
        isRunning = false;
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
                memory.init(controlInputStream.read());
                while (isRunning && controlSocket.isConnected() && dataSocket.isConnected()) {
                    // add decode logic
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } finally {
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

}
