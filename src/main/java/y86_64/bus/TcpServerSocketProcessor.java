package y86_64.bus;

import y86_64.Closeable;
import y86_64.Component;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;

import static java.util.Arrays.stream;

public abstract class TcpServerSocketProcessor<C extends Component> {

    protected final TcpBus[] tcpBuses = new TcpBus[3];
    protected Thread thread = null;
    protected TcpServer<C> tcpServer;
    protected C component;

    public void start() {
        if (thread != null) {
            throw new IllegalStateException("Server socket is already running.");
        }
        thread = new Thread(() -> {
            try {
                while (tcpServer.isRunning() && run()) {
                    // wait for
                }
            } catch (InterruptedIOException e) {
                // log interrupt
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } finally {
                close();
            }
        });
        thread.start();
    }

    public void close() {
        TransportUtil.closeResourcesWithWrappedExceptions(stream(tcpBuses).toArray(Closeable[]::new));
    }

    abstract protected boolean run() throws IOException;

}
