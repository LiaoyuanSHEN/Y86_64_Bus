package y86_64.bus;

import y86_64.Bus;
import y86_64.Closeable;
import y86_64.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BusTcpImpl implements Bus {

    private final List<Closeable> tcpServers = new LinkedList<>();

    @Override
    public void registerComponent(long componentId, Component component) {
        try {
            TcpServer tcpServer = ComponentTcpFactory.getTcpServer(componentId, component);
            tcpServer.run();
            tcpServers.add(tcpServer);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T extends Component> T getComponent(long componentId, String host) {
        return ComponentTcpFactory.getComponentClient(componentId, host);
    }

    @Override
    public void stop() {
        TransportUtil.closeResourcesWithWrappedExceptions(tcpServers.toArray(new Closeable[0]));
    }
}
