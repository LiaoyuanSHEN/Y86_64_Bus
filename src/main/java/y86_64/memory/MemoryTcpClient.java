package y86_64.memory;

import y86_64.Memory;
import y86_64.bus.TcpBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    }

    @Override
    public void init(long component) {
        try {
            controlBus.writeValue(component);
        } catch (IOException e) {
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
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void write(long address, long value) {
        try {
            controlBus.writeValue(READ_FLAG);
            addressBus.writeValue(address);
            dataBus.writeValue(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() {
        Collection<Exception> exceptions = new LinkedList<>();
        exceptions.add(controlBus.close());
        exceptions.add(addressBus.close());
        exceptions.add(dataBus.close());
        exceptions = exceptions.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (exceptions.size() > 0) {
            throw new IllegalStateException(exceptions.toString());
        }
    }
}
