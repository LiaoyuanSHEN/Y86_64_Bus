package y86_64.bus;

import y86_64.Closeable;
import y86_64.Component;

import static java.util.Arrays.stream;

public abstract class TcpClient implements Component {

    protected final TcpBus[] tcpBuses = new TcpBus[3];

    @Override
    public void stop() {
        TransportUtil.closeResourcesWithWrappedExceptions(stream(tcpBuses).toArray(Closeable[]::new));
    }

}
