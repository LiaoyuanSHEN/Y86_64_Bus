package y86_64.bus;

import y86_64.Closeable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class TransportUtil {

    private static final long MASK = 0B1111_1111;

    private TransportUtil() {
    }

    public static void writeLongToOutputStream(long value, OutputStream out) throws IOException {
        out.write((int) (value >> 56));
        out.write((int) (value >> 48 & MASK));
        out.write((int) (value >> 40 & MASK));
        out.write((int) (value >> 32 & MASK));
        out.write((int) (value >> 24 & MASK));
        out.write((int) (value >> 16 & MASK));
        out.write((int) (value >> 8 & MASK));
        out.write((int) (value & MASK));
    }

    public static long readLongFromInputStream(InputStream in) throws IOException {
        long value = in.read();
        value <<= 8;
        value |= in.read();
        value <<= 8;
        value |= in.read();
        value <<= 8;
        value |= in.read();
        value <<= 8;
        value |= in.read();
        value <<= 8;
        value |= in.read();
        value <<= 8;
        value |= in.read();
        value <<= 8;
        value |= in.read();
        return value;
    }

    public static void closeResourcesWithWrappedExceptions(Closeable... resources) {
        List<Exception> exceptions = new LinkedList<>();
        for (Closeable resource : resources) {
            try {
                resource.stop();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        if (!exceptions.isEmpty()) {
            throw new IllegalStateException(exceptions.toString());
        }
    }
}
