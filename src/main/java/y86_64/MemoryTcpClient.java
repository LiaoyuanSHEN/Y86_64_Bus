package y86_64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MemoryTcpClient implements Memory {

    private final Socket controlClient;
    private final Socket dataClient;
    private final InputStream controlInputStream;
    private final OutputStream controlOutputStream;
    private final InputStream dataInputStream;
    private final OutputStream dataOutputStream;

    public MemoryTcpClient(int port) throws IOException {
        this("localhost", port);
    }

    public MemoryTcpClient(String host, int port) throws IOException {
        controlClient = new Socket(host, MemoryConst.CONTROL_PORT);
        dataClient = new Socket(host, port);
        controlInputStream = controlClient.getInputStream();
        controlOutputStream = controlClient.getOutputStream();
        dataInputStream = dataClient.getInputStream();
        dataOutputStream = dataClient.getOutputStream();
    }

    @Override
    public void init(int component) {
        try {
            controlOutputStream.write(component);
            controlOutputStream.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long read(long address) {
        try {
            controlOutputStream.write((int) (address >> 32));
            controlOutputStream.write((int) (address & 0B1111_1111_1111_1111_1111_1111_1111));
            controlOutputStream.flush();
            long value = dataInputStream.read();
            value = value << 32;
            value &= dataInputStream.read();
            return value;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void write(long address, long value) {
        try {
            controlOutputStream.write((int) (address >> 32));
            controlOutputStream.write((int) (address & 0B1111_1111_1111_1111_1111_1111_1111));
            controlOutputStream.flush();
            dataOutputStream.write((int) (value >> 32));
            dataOutputStream.write((int) (value & 0B1111_1111_1111_1111_1111_1111_1111));
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
