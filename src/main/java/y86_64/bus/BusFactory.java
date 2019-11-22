package y86_64.bus;

import y86_64.Bus;

public class BusFactory {

    private static final Bus BUS = new BusTcpImpl();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(BUS::stop));
    }

    public static Bus getBus() {
        return BUS;
    }

}
