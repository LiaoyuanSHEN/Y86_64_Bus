package y86_64.bus;

import y86_64.exceptions.*;

import static y86_64.bus.BusConst.*;

public class ComponentControlCodeProcessor {

    private ComponentControlCodeProcessor() {}

    public static void handleException(long errorCode) throws ComponentException {
        switch ((int) errorCode) {
            case NO_ERROR:
                return;
            case COMPONENT_INITIALIZE_EXCEPTION:
                throw new ComponentInitializeException();
            case CPU_COMPUTE_EXCEPTION:
                throw new CpuComputeException();
            case CPU_INTERRUPT_EXCEPTION:
                throw new CpuInterruptException();
            case CPU_UNKNOWN_EXCEPTION:
                throw new CpuException("Unknown CPU exception");
            case MEMORY_READ_EXCEPTION:
                throw new MemoryReadException();
            case MEMORY_WRITE_EXCEPTION:
                throw new MemoryWriteException();
            case MEMORY_OUT_OF_BOUND_EXCEPTION:
                throw new MemoryOutOfBoundException();
            case MEMORY_UNKNOWN_EXCEPTION:
                throw new MemoryException("Unknown memory exception");
            default:
                throw new ComponentException("Unknown component exception: " + errorCode);
        }
    }

    public static long toExceptionCode(ComponentException e) {
        if (e instanceof ComponentInitializeException) {
            return COMPONENT_INITIALIZE_EXCEPTION;
        }
        if (e instanceof CpuComputeException) {
            return CPU_COMPUTE_EXCEPTION;
        }
        if (e instanceof CpuInterruptException) {
            return CPU_INTERRUPT_EXCEPTION;
        }
        if (e instanceof CpuException) {
            return CPU_UNKNOWN_EXCEPTION;
        }
        if (e instanceof MemoryOutOfBoundException) {
            return MEMORY_OUT_OF_BOUND_EXCEPTION;
        }
        if (e instanceof MemoryReadException) {
            return MEMORY_READ_EXCEPTION;
        }
        if (e instanceof MemoryWriteException) {
            return MEMORY_WRITE_EXCEPTION;
        }
        if (e instanceof MemoryException) {
            return MEMORY_UNKNOWN_EXCEPTION;
        }
        return COMPONENT_UNKNOWN_EXCEPTION;
    }
}
