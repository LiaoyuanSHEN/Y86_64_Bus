package y86_64;

import y86_64.memory.MemoryTcpClient;
import y86_64.memory.MemoryTcpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import static y86_64.BusConst.*;

public class BusTcpClientImpl implements Bus {
    private MemoryTcpServer memoryTcpServer = null;
    private List<Thread> threads = new LinkedList<>();

    @Override
    public void registerMemory(Memory memory) {
        try {
            memoryTcpServer = new MemoryTcpServer(memory);
            Thread thread = new Thread(memoryTcpServer);
            thread.start();
            threads.add(thread);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void registerCPU(CPU cpu) {

    }

    @Override
    public Memory getMemory() {
        try {
            return new MemoryTcpClient();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CPU getCPU() {
        return null;
    }

    @Override
    public void stop() {
        if (memoryTcpServer != null) {
            memoryTcpServer.stop();
        }
    }
}
