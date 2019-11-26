package y86_64.bus;

import y86_64.Closeable;
import y86_64.Component;

import java.io.IOException;

public abstract class TcpServer<C extends Component> implements Closeable {

    protected final C component;

    public TcpServer(C component) {
        this.component = component;
    }

    public abstract void run() throws IOException;

    public abstract boolean isRunning();

}
