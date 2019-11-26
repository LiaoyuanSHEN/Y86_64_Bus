package y86_64.cpu;

import y86_64.CPU;
import y86_64.bus.ComponentControlCodeProcessor;
import y86_64.bus.TcpBus;
import y86_64.bus.TcpServerSocketProcessor;
import y86_64.exceptions.CpuException;

import java.io.IOException;
import java.net.Socket;

import static y86_64.bus.BusConst.*;

public class CpuTcpServerSocketProcessor extends TcpServerSocketProcessor<CPU> {

    public CpuTcpServerSocketProcessor(CPU cpu, CpuTcpServer server, Socket controlSocket, Socket dataSocket) throws IOException {
        component = cpu;
        tcpServer = server;
        tcpBuses[CONTROL_BUS_INDEX] = new TcpBus(controlSocket);
        tcpBuses[DATA_BUS_INDEX] = new TcpBus(dataSocket);
    }

    @Override
    protected boolean start() throws IOException {
        try {
            long controlCode = tcpBuses[CONTROL_BUS_INDEX].readValue();
            switch ((int) controlCode) {
                case CONNECTION_CLOSED:
                    System.out.println("Client connection closed.");
                    return false;
                case CPU_COMPUTE_CODE:
                    component.compute();
                    break;
                case CPU_INTERRUPT_CODE:
                    component.interrupt(tcpBuses[DATA_BUS_INDEX].readValue());
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized controlCode: " + controlCode);
            }
            tcpBuses[CONTROL_BUS_INDEX].writeValue(NO_ERROR);
            return true;
        } catch (CpuException e) {
            tcpBuses[CONTROL_BUS_INDEX].writeValue(ComponentControlCodeProcessor.toExceptionCode(e));
            return false;
        }
    }
}
