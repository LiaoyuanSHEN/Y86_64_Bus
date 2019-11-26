package y86_64.bus;

import y86_64.Closeable;
import y86_64.Component;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Arrays;

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
