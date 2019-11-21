package y86_64.bus;

import y86_64.Bus;

public class BusFactory {

    public static Bus getBus() {
        return new BusTcpImpl();
    }

}
