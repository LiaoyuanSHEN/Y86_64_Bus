package y86_64.memory;

import y86_64.Bus;
import y86_64.BusTcpClientImpl;
import y86_64.Memory;
import y86_64.exceptions.MemoryException;

public class MemoryServerTest {

    public static void main(String[] args) {
        Bus bus = new BusTcpClientImpl();
        bus.registerMemory(new MemoryMock());
    }

    static class MemoryMock implements Memory {

        @Override
        public void init(int component) throws MemoryException {
            System.out.println(component);
        }

        @Override
        public long read(long address) throws MemoryException {
            System.out.println(address);
            return address;
        }

        @Override
        public void write(long address, long value) throws MemoryException {
            System.out.println(address);
            System.out.println(value);
        }
    }

}
