package y86_64;

import y86_64.Bus;
import y86_64.BusTcpClientImpl;
import y86_64.Memory;

public class MemoryClientTest {

    public static void main(String[] args) throws Throwable {
        Bus bus = new BusTcpClientImpl();
        Memory memory = bus.getMemory();
        memory.init(0);
        System.out.println(memory.read(123));
        memory.write(123, 456);

    }

}
