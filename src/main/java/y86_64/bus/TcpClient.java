package y86_64.bus;

import y86_64.Closeable;
import y86_64.Component;
import y86_64.exceptions.ComponentException;
import y86_64.exceptions.CpuException;

import java.io.IOException;
import java.util.Arrays;

import static y86_64.bus.BusConst.CONTROL_BUS_INDEX;
import static y86_64.bus.BusConst.CPU_COMPUTE_CODE;

public abstract class TcpClient implements Component {

    protected final TcpBus[] tcpBuses = new TcpBus[3];

    @Override
    public void init(long component) throws ComponentException {
        try {
            tcpBuses[CONTROL_BUS_INDEX].writeValue(CPU_COMPUTE_CODE);
            ComponentControlCodeProcessor.handleException(tcpBuses[CONTROL_BUS_INDEX].readValue());
        } catch (CpuException e) {
            stop();
            throw e;
        } catch (IOException e) {
            stop();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() {
        TransportUtil.closeResourcesWithWrappedExceptions(Arrays.stream(tcpBuses).toArray(Closeable[]::new));
    }

}
