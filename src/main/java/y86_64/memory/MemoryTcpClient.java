package y86_64.memory;

import y86_64.Memory;
import y86_64.bus.TcpBus;
import y86_64.bus.TransportUtil;

import java.io.IOException;
import java.net.Socket;

import static y86_64.memory.MemoryConst.*;

public class MemoryTcpClient implements Memory {

    private final TcpBus controlBus;
    private final TcpBus dataBus;
    private final TcpBus addressBus;

    public MemoryTcpClient() throws IOException {
        this("localhost");
    }

    public MemoryTcpClient(String host) throws IOException {
        controlBus = new TcpBus(new Socket(host, CONTROL_PORT));
        dataBus = new TcpBus(new Socket(host, DATA_PORT));
        addressBus = new TcpBus(new Socket(host, ADDRESS_PORT));
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    @Override
    public void init(long component) {
        try {
            controlBus.writeValue(component);
        } catch (IOException e) {
            stop();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long read(long address) {
        try {
            controlBus.writeValue(READ_FLAG);
            addressBus.writeValue(address);
            return dataBus.readValue();
        } catch (IOException e) {
            stop();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void write(long address, long value) {
        try {
            controlBus.writeValue(WRITE_FLAG);
            addressBus.writeValue(address);
            dataBus.writeValue(value);
        } catch (IOException e) {
            stop();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() {
        TransportUtil.closeResourcesWithWrappedExceptions("close", controlBus, addressBus, dataBus);
    }
}
