package y86_64.bus;

import y86_64.Closeable;
import y86_64.util.TranslateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class TcpBus implements Closeable {

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public TcpBus(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public long readValue() throws IOException {
        byte[] arr = new byte[8];
        if (inputStream.read(arr) == -1 ) {
            throw new SocketException("Socket closed.");
        }
        return TranslateUtil.fromLongByteArray(arr);
    }

    public void writeValue(long value) throws IOException {
        outputStream.write(TranslateUtil.toLongByteArray(value));
        outputStream.flush();
    }

    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }

    @Override
    public void stop() throws IOException {
        if (socket.isConnected()) {
            socket.close();
        }
    }

}
