package y86_64.memory;

import y86_64.Memory;
import y86_64.bus.ComponentControlCodeProcessor;
import y86_64.bus.TcpBus;
import y86_64.bus.TcpClient;
import y86_64.exceptions.MemoryException;

import java.io.IOException;
import java.net.Socket;

import static y86_64.bus.BusConst.*;

public class MemoryTcpClient extends TcpClient implements Memory {

    public MemoryTcpClient() throws IOException {
        this("localhost");
    }

    public MemoryTcpClient(String host) throws IOException {
        tcpBuses[CONTROL_BUS_INDEX] = new TcpBus(new Socket(host, MEMORY_CONTROL_PORT));
        tcpBuses[DATA_BUS_INDEX] = new TcpBus(new Socket(host, MEMORY_DATA_PORT));
        tcpBuses[ADDRESS_BUS_INDEX] = new TcpBus(new Socket(host, MEMORY_ADDRESS_PORT));
    }

    @Override
    public byte readByte(long address) throws MemoryException {
        try {
            tcpBuses[CONTROL_BUS_INDEX].writeValue(MEMORY_READ_CODE);
            tcpBuses[ADDRESS_BUS_INDEX].writeValue(address);
            ComponentControlCodeProcessor.handleException(tcpBuses[CONTROL_BUS_INDEX].readValue());
            return (byte) tcpBuses[DATA_BUS_INDEX].readValue();
        } catch (MemoryException e) {
            stop();
            throw e;
        } catch (Exception e) {
            stop();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void writeByte(long address, byte value) throws MemoryException {
        try {
            tcpBuses[CONTROL_BUS_INDEX].writeValue(MEMORY_WRITE_CODE);
            tcpBuses[ADDRESS_BUS_INDEX].writeValue(address);
            tcpBuses[DATA_BUS_INDEX].writeValue(value);
            ComponentControlCodeProcessor.handleException(tcpBuses[CONTROL_BUS_INDEX].readValue());
        } catch (MemoryException e) {
            stop();
            throw e;
        } catch (Exception e) {
            stop();
            throw new IllegalStateException(e);
        }
    }

}
