package y86_64.bus;

import y86_64.Component;

import java.io.IOException;

public abstract class TcpServer<C extends Component> {

    protected final C component;

    public TcpServer(C component) {
        this.component = component;
    }

    abstract public void start() throws IOException;

    abstract public void stop() throws IOException;

}
