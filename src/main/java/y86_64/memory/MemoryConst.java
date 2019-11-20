package y86_64.memory;

import y86_64.exceptions.MemoryException;
import y86_64.exceptions.MemoryNotInitializedException;
import y86_64.exceptions.MemoryOutOfBoundException;

public class MemoryConst {

    public static final int CONTROL_PORT = 15616;
    public static final int WRITE_FLAG = 1;
    public static final int READ_FLAG = 2;

    public static final int UNKNOWN_ERROR = -1;
    public static final int NO_ERROR = 0;
    public static final int MEMORY_NOT_INITIALIZED_EXCEPTION = 10;
    public static final int MEMORY_OUT_OF_BOUND_EXCEPTION = 11;

    private MemoryConst() {}

    public static MemoryException getExceptionMessage(int errorCode) {
        switch (errorCode) {
            case MEMORY_NOT_INITIALIZED_EXCEPTION:
                return new MemoryNotInitializedException();
            case MEMORY_OUT_OF_BOUND_EXCEPTION:
                return new MemoryOutOfBoundException();
            default:
                return new MemoryException("Unknown memory exception");
        }
    }

    public static int toExceptionCode(MemoryException e) {
        if (e instanceof MemoryNotInitializedException) {
            return MEMORY_NOT_INITIALIZED_EXCEPTION;
        }
        if (e instanceof MemoryOutOfBoundException) {
            return MEMORY_OUT_OF_BOUND_EXCEPTION;
        }
        return -1;
    }

}
