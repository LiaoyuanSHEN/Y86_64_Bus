package y86_64.cpu;

import y86_64.CPU;
import y86_64.bus.TcpServer;

import java.io.IOException;

public class CpuTcpServer extends TcpServer<CPU> {

    public CpuTcpServer(CPU cpu) throws IOException {
        super(cpu);
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() throws IOException {

    }

}
