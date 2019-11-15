package y86_64;

import java.io.IOException;
import java.net.ServerSocket;

public class BusTcpServer {

    private final ServerSocket server;

    public BusTcpServer() throws IOException {
        this(BusConst.LISTEN_PORT);
    }

    public BusTcpServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    public void run() {
        while (true) {

        }
    }

}
