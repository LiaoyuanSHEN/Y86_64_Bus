package y86_64.memory;

import y86_64.Memory;
import y86_64.TransportUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static y86_64.memory.MemoryConst.*;

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
        controlClient = new Socket(host, CONTROL_PORT);
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
            controlOutputStream.write(READ_FLAG);
            controlOutputStream.flush();
            TransportUtil.writeLongToOutputStream(address, controlOutputStream);
            controlOutputStream.flush();
            return TransportUtil.readLongFromInputStream(dataInputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void write(long address, long value) {
        try {
            controlOutputStream.write(WRITE_FLAG);
            controlOutputStream.flush();
            TransportUtil.writeLongToOutputStream(address, controlOutputStream);
            controlOutputStream.flush();
            TransportUtil.writeLongToOutputStream(value, dataOutputStream);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
