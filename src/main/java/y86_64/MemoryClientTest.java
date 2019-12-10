package y86_64;

import y86_64.bus.BusFactory;

public class MemoryClientTest {

    public static void main(String[] args) throws Throwable {
        Bus bus = BusFactory.getBus();
        Memory memory = bus.getComponent(ComponentId.MEMORY);
        System.out.println(memory.readByte(123));
        memory.writeByte(123, (byte) 12);
    }

}
