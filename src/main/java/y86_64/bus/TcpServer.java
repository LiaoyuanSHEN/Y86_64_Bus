package y86_64.bus;

import y86_64.Component;

import java.io.IOException;

public abstract class TcpServer<C extends Component> {

    protected final C component;

    public TcpServer(C component) {
        this.component = component;
    }

    public abstract void start() throws IOException;

    public abstract void stop() throws IOException;

    public abstract boolean isRunning();

}
