package y86_64.bus.factory;

import y86_64.CPU;
import y86_64.Component;
import y86_64.ComponentId;
import y86_64.Memory;
import y86_64.bus.TcpServer;
import y86_64.memory.MemoryTcpServer;
import y86_64.cpu.CpuTcpServer;

import java.io.IOException;

public class ComponentTcpServerFactory {

    public static TcpServer getTcpServer(long componentId, Component component) {
        switch ((int) componentId) {
            case ComponentId.MEMORY:
                    try {
                        return new MemoryTcpServer((Memory) component);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
            case ComponentId.CPU:
                    try {
                        return new CpuTcpServer((CPU) component);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
            default:
                throw new IllegalStateException("Unsupported componentId: + " + componentId);
        }
    }

}
