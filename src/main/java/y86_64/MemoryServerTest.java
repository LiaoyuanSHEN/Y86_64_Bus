package y86_64;

import y86_64.bus.BusFactory;
import y86_64.exceptions.MemoryException;

public class MemoryServerTest {

    public static void main(String[] args) {
        Bus bus = BusFactory.getBus();
        bus.registerComponent(ComponentId.MEMORY, new MemoryMock());
    }

    static class MemoryMock implements Memory {

        @Override
        public byte readByte(long address) throws MemoryException {
            System.out.println(address);
            return (byte) address;
        }

        @Override
        public void writeByte(long address, byte value) throws MemoryException {
            System.out.println(address);
            System.out.println(value);
        }

        @Override
        public void stop() {

        }
    }

}
