package y86_64.bus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpBus {

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public TcpBus(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public long readValue() throws IOException {
        return TransportUtil.readLongFromInputStream(inputStream);
    }

    public void writeValue(long value) throws IOException {
        TransportUtil.writeLongToOutputStream(value, outputStream);
        outputStream.flush();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public IOException close() {
        if (socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                return e;
            }
        }
        return null;
    }

}
