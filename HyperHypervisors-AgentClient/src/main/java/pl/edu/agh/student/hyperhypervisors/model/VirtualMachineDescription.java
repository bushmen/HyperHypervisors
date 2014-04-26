package pl.edu.agh.student.hyperhypervisors.model;

import org.virtualbox_4_3.DeviceType;
import org.virtualbox_4_3.IMachine;
import org.virtualbox_4_3.IMedium;
import org.virtualbox_4_3.IMediumAttachment;

import java.io.Serializable;

public class VirtualMachineDescription implements Serializable {
    private static final int EMPTY = 0;

    private String name;
    private String operationSystem;
    private long memorySize;
    private long cpuCount;
    private long diskSpace;

    public VirtualMachineDescription(IMachine machine) {
        this.name = machine.getName();
        this.operationSystem = machine.getOSTypeId();
        this.memorySize = machine.getMemorySize();
        this.cpuCount = machine.getCPUCount();
        diskSpace = getDiskSpace(machine);
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

    @Override
    public String toString() {
        return String.format("Name: %s\nOS: %s\nHard disk size: %dKB\nRAM size: %d\nCPU's: %d", name, operationSystem, diskSpace, memorySize, cpuCount);
    }
}
