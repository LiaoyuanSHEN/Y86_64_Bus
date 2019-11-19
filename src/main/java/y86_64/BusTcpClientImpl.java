package y86_64;

import y86_64.memory.MemoryTcpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static y86_64.BusConst.*;

public class BusTcpClientImpl implements Bus, Runnable {

    private final Socket busSocket;
    private final InputStream busInputStream;
    private final OutputStream busOutputStream;
    private MemoryTcpServer memoryTcpServer;

    public BusTcpClientImpl(Socket busSocket) throws IOException {
        this.busSocket = busSocket;
        this.busInputStream = busSocket.getInputStream();
        this.busOutputStream = busSocket.getOutputStream();
    }

    @Override
    public void registerMemory(Memory memory) {
        try {
            memoryTcpServer = new MemoryTcpServer(MEMORY_LISTEN_PORT, memory);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void registerCPU(CPU cpu) {

    }

    @Override
    public Memory getMemory() {
        return null;
    }

    @Override
    public CPU getCPU() {
        return null;
    }

    @Override
    public void run() {

    }
}
