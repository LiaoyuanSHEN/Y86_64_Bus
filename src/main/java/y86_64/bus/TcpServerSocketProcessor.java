package y86_64.bus;

import y86_64.Closeable;
import y86_64.Component;
import y86_64.exceptions.ComponentException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Arrays;

import static y86_64.bus.BusConst.*;

public abstract class TcpServerSocketProcessor<C extends Component> {

    protected final TcpBus[] tcpBuses = new TcpBus[3];
    protected Thread thread = null;
    protected TcpServer<C> tcpServer;
    protected C component;

    public void run() {
        if (thread != null) {
            throw new IllegalStateException("Server socket is already running.");
        }
        thread = new Thread(() -> {
            try {
                try {
                    component.init(tcpBuses[CONTROL_BUS_INDEX].readValue());
                    tcpBuses[CONTROL_BUS_INDEX].writeValue(NO_ERROR);
                } catch (ComponentException e) {
                    tcpBuses[CONTROL_BUS_INDEX].writeValue(COMPONENT_INITIALIZE_EXCEPTION);
                    // log error
                    throw new IllegalStateException("Component initial failed.");
                }
                while (tcpServer.isRunning() && start()) {
                    // wait for
                }
            } catch (InterruptedIOException e) {
                // log interrupt
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } finally {
                close();
            }
        });
        thread.start();
    }

    public void close() {
        TransportUtil.closeResourcesWithWrappedExceptions(Arrays.stream(tcpBuses).toArray(Closeable[]::new));
    }

    abstract protected boolean start() throws IOException;

}
