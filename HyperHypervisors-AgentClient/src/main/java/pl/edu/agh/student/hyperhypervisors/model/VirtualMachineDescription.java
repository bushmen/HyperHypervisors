package pl.edu.agh.student.hyperhypervisors.model;

import org.virtualbox_4_3.DeviceType;
import org.virtualbox_4_3.IMachine;
import org.virtualbox_4_3.IMedium;
import org.virtualbox_4_3.IMediumAttachment;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class VirtualMachineDescription implements Serializable {
    private static final int EMPTY = 0;

    private String name;
    private String operationSystem;
    private long memorySize;
    private long cpuCount;
    private long diskSpace;

    public VirtualMachineDescription() {
    }

    public VirtualMachineDescription(IMachine machine) {
        this(machine.getName(), machine.getOSTypeId(), machine.getMemorySize(), machine.getCPUCount(), 0L);
        diskSpace = getDiskSpace(machine);
    }

    @ConstructorProperties(value = {"name", "operationSystem", "memorySize", "cpuCount", "diskSpace"})
    public VirtualMachineDescription(String name, String operationSystem, long memorySize, long cpuCount, long diskSpace) {
        this.name = name;
        this.operationSystem = operationSystem;
        this.memorySize = memorySize;
        this.cpuCount = cpuCount;
        this.diskSpace = diskSpace;
    }

    private long getDiskSpace(IMachine machine) {
        for (IMediumAttachment attachment : machine.getMediumAttachments()) {
            if (isHardDisk(attachment)) {
                IMedium medium = attachment.getMedium();
                if (medium != null) {
                    return medium.getSize();
                }
            }
        }

        return EMPTY;
    }

    private boolean isHardDisk(IMediumAttachment attachment) {
        return attachment.getType() == DeviceType.HardDisk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperationSystem() {
        return operationSystem;
    }

    public void setOperationSystem(String operationSystem) {
        this.operationSystem = operationSystem;
    }

    public long getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(long diskSpace) {
        this.diskSpace = diskSpace;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public long getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(long cpuCount) {
        this.cpuCount = cpuCount;
    }

    @Override
    public String toString() {
        return String.format("Name: %s\nOS: %s\nHard disk size: %dKB\nRAM size: %d\nCPU's: %d", name, operationSystem, diskSpace, memorySize, cpuCount);
    }
}
