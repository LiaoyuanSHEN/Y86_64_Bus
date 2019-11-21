package y86_64;

import y86_64.bus.BusFactory;
import y86_64.exceptions.MemoryException;

public class MemoryServerTest {

    public static void main(String[] args) {
        Bus bus = BusFactory.getBus();
    }

    static class MemoryMock implements Memory {

        @Override
        public void init(long component) throws MemoryException {
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

        @Override
        public void stop() {

        }
    }

}
