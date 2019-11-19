package y86_64.memory;

import y86_64.Memory;

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
            controlOutputStream.write((int) (address >> 56));
            controlOutputStream.write((int) (address >> 48 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 40 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 32 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 24 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 16 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 8 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.flush();
            long value = dataInputStream.read();
            value <<= 8;
            value &= dataInputStream.read();
            value <<= 8;
            value &= dataInputStream.read();
            value <<= 8;
            value &= dataInputStream.read();
            value <<= 8;
            value &= dataInputStream.read();
            value <<= 8;
            value &= dataInputStream.read();
            value <<= 8;
            value &= dataInputStream.read();
            value <<= 8;
            value &= dataInputStream.read();
            return value;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void write(long address, long value) {
        try {
            controlOutputStream.write(WRITE_FLAG);
            controlOutputStream.flush();
            controlOutputStream.write((int) (address >> 56));
            controlOutputStream.write((int) (address >> 48 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 40 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 32 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 24 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 16 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address >> 8 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.write((int) (address & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            controlOutputStream.flush();
            dataOutputStream.write((int) (value >> 56));
            dataOutputStream.write((int) (value >> 48 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            dataOutputStream.write((int) (value >> 40 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            dataOutputStream.write((int) (value >> 32 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            dataOutputStream.write((int) (value >> 24 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            dataOutputStream.write((int) (value >> 16 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            dataOutputStream.write((int) (value >> 8 & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            dataOutputStream.write((int) (value & 0B0000_0000_0000_0000_0000_0000_0000_1111));
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
