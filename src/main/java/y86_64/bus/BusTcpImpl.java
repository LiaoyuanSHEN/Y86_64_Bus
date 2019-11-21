package y86_64.bus;

import y86_64.Bus;
import y86_64.Component;
import y86_64.bus.factory.ComponentTcpServerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BusTcpImpl implements Bus {

    private final List<TcpServer> tcpServers = new LinkedList<>();

    @Override
    public void registerComponent(long componentId, Component component) {
        try {
            TcpServer tcpServer = ComponentTcpServerFactory.getTcpServer(componentId, component);
            tcpServer.start();
            tcpServers.add(tcpServer);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T extends Component> T getComponent(long l, String s) {
        return null;
    }

    @Override
    public void stop() {
        List<IOException> exceptions = new LinkedList<>();
        for (TcpServer tcpServer : tcpServers) {
            try {
                tcpServer.stop();
            } catch (IOException e) {
                exceptions.add(e);
            }
        }
        if (exceptions.size() > 0) {
            throw new IllegalStateException(exceptions.toString());
        }
    }

}
