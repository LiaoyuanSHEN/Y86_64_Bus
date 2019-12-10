package y86_64.bus;

import y86_64.Closeable;

import java.util.LinkedList;
import java.util.List;

public class TransportUtil {

    private TransportUtil() {}

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
