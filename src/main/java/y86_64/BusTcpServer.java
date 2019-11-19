package y86_64;

import java.io.IOException;
import java.net.ServerSocket;

public class BusTcpServer implements Runnable {

    private final ServerSocket server;
    private boolean running = false;

    public BusTcpServer() throws IOException {
        this(BusConst.BUS_LISTEN_PORT);
    }

    public BusTcpServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                server.accept();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void stop() {
        running = false;
    }

}
