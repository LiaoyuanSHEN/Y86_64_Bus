package y86_64.bus;

public class BusConst {

    public static final int MEMORY_CONTROL_PORT = 11341;
    public static final int MEMORY_DATA_PORT = 11342;
    public static final int MEMORY_ADDRESS_PORT = 11343;
    public static final int CPU_CONTROL_PORT = 12341;
    public static final int CPU_DATA_PORT = 12342;

    public static final int CONTROL_BUS_INDEX = 0;
    public static final int DATA_BUS_INDEX = 1;
    public static final int ADDRESS_BUS_INDEX = 2;

    public static final int CPU_COMPUTE_CODE = 1;
    public static final int CPU_INTERRUPT_CODE = 2;
    public static final int MEMORY_READ_CODE = 5;
    public static final int MEMORY_WRITE_CODE = 6;

    public static final int CONNECTION_CLOSED = -1;
    public static final int NO_ERROR = 0;
    public static final int COMPONENT_UNKNOWN_EXCEPTION = 10;
    public static final int COMPONENT_INITIALIZE_EXCEPTION = 11;
    public static final int CPU_UNKNOWN_EXCEPTION = 20;
    public static final int CPU_COMPUTE_EXCEPTION = 21;
    public static final int CPU_INTERRUPT_EXCEPTION = 22;
    public static final int MEMORY_READ_EXCEPTION = 30;
    public static final int MEMORY_WRITE_EXCEPTION = 31;
    public static final int MEMORY_UNKNOWN_EXCEPTION = 32;
    public static final int MEMORY_OUT_OF_BOUND_EXCEPTION = 33;

}
