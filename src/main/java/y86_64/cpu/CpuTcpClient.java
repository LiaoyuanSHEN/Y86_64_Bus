package y86_64.cpu;

import y86_64.CPU;
import y86_64.bus.ComponentControlCodeProcessor;
import y86_64.bus.TcpBus;
import y86_64.bus.TcpClient;
import y86_64.exceptions.CpuException;

import java.io.IOException;
import java.net.Socket;

import static y86_64.bus.BusConst.*;

public class CpuTcpClient extends TcpClient implements CPU {

    public CpuTcpClient() throws IOException {
        this("localhost");
    }

    public CpuTcpClient(String host) throws IOException {
        tcpBuses[CONTROL_BUS_INDEX] = new TcpBus(new Socket(host, CPU_CONTROL_PORT));
        tcpBuses[DATA_BUS_INDEX] = new TcpBus(new Socket(host, CPU_DATA_PORT));
    }

    @Override
    public void compute() throws CpuException {
        try {
            tcpBuses[CONTROL_BUS_INDEX].writeValue(CPU_COMPUTE_CODE);
            ComponentControlCodeProcessor.handleException(tcpBuses[CONTROL_BUS_INDEX].readValue());
        } catch (CpuException e) {
            stop();
            throw e;
        } catch (Exception e) {
            stop();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void interrupt(long code) throws CpuException {
        try {
            tcpBuses[CONTROL_BUS_INDEX].writeValue(CPU_INTERRUPT_CODE);
            tcpBuses[DATA_BUS_INDEX].writeValue(code);
            ComponentControlCodeProcessor.handleException(tcpBuses[CONTROL_BUS_INDEX].readValue());
        } catch (CpuException e) {
            stop();
            throw e;
        } catch (Exception e) {
            stop();
            throw new IllegalStateException(e);
        }
    }
}
