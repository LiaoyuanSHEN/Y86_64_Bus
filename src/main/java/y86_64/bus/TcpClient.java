package y86_64.bus;

import y86_64.Component;

import java.io.IOException;

public abstract class TcpClient<C extends Component> {

    protected final C component;

    public TcpClient(C component) {
        this.component = component;
    }

    public abstract void stop() throws IOException;

}
