package y86_64.memory;

import y86_64.Memory;
import y86_64.bus.ComponentControlCodeProcessor;
import y86_64.bus.TcpBus;
import y86_64.bus.TcpServerSocketProcessor;
import y86_64.exceptions.MemoryException;

import java.io.IOException;
import java.net.Socket;

import static y86_64.bus.BusConst.*;

public class MemoryTcpServerSocketProcessor extends TcpServerSocketProcessor<Memory> {

    public MemoryTcpServerSocketProcessor(Memory memory, MemoryTcpServer memoryTcpServer, Socket controlSocket, Socket dataSocket, Socket addressSocket) throws IOException {
        component = memory;
        tcpServer = memoryTcpServer;
        tcpBuses[CONTROL_BUS_INDEX] = new TcpBus(controlSocket);
        tcpBuses[DATA_BUS_INDEX] = new TcpBus(dataSocket);
        tcpBuses[ADDRESS_BUS_INDEX] = new TcpBus(addressSocket);
    }

    @Override
    protected boolean run() throws IOException {
        try {
            long controlCode = tcpBuses[CONTROL_BUS_INDEX].readValue();
            switch ((int) controlCode) {
                case CONNECTION_CLOSED:
                    System.out.println("Client connection closed.");
                    return false;
                case MEMORY_READ_CODE:
                    long value = component.readByte(tcpBuses[ADDRESS_BUS_INDEX].readValue());
                    tcpBuses[DATA_BUS_INDEX].writeValue(value);
                    break;
                case MEMORY_WRITE_CODE:
                    component.writeByte(tcpBuses[ADDRESS_BUS_INDEX].readValue(), (byte) tcpBuses[DATA_BUS_INDEX].readValue());
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized controlCode: " + controlCode);
            }
            tcpBuses[CONTROL_BUS_INDEX].writeValue(NO_ERROR);
            return true;
        } catch (MemoryException e) {
            tcpBuses[CONTROL_BUS_INDEX].writeValue(ComponentControlCodeProcessor.toExceptionCode(e));
            return false;
        }
    }
}
