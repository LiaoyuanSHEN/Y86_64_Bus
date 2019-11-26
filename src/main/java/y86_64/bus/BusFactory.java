package y86_64.bus;

import y86_64.Bus;

import java.io.IOException;

public class BusFactory {

    private static final Bus BUS = new BusTcpImpl();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                BUS.stop();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }));
    }

    public static Bus getBus() {
        return BUS;
    }

}
