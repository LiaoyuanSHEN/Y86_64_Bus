package y86_64;

import y86_64.bus.BusFactory;

public class MemoryClientTest {

    public static void main(String[] args) throws Throwable {
        Bus bus = BusFactory.getBus();
        Memory memory = bus.getComponent(ComponentId.MEMORY);
        memory.init(0);
        System.out.println(memory.read(123));
        memory.write(123, 456);

    }

}
