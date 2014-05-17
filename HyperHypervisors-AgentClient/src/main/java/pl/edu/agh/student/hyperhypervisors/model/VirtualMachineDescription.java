package pl.edu.agh.student.hyperhypervisors.model;

import org.virtualbox_4_3.DeviceType;
import org.virtualbox_4_3.IMachine;
import org.virtualbox_4_3.IMedium;
import org.virtualbox_4_3.IMediumAttachment;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VirtualMachineDescription implements Serializable {
    private String name;
    private String operationSystem;
    private long memorySize;
    private long cpuCount;
    private Map<String, Long> disksSpace;

    public VirtualMachineDescription() {
    }

    public VirtualMachineDescription(IMachine machine) {
        this(machine.getName(), machine.getOSTypeId(), machine.getMemorySize(), machine.getCPUCount(), null);
        disksSpace = getDisksSpace(machine);
    }

    @ConstructorProperties(value = {"name", "operationSystem", "memorySize", "cpuCount", "disksSpace"})
    public VirtualMachineDescription(String name, String operationSystem, long memorySize, long cpuCount, Map<String, Long> disksSpace) {
        this.name = name;
        this.operationSystem = operationSystem;
        this.memorySize = memorySize;
        this.cpuCount = cpuCount;
        this.disksSpace = disksSpace;
    }

    private Map<String, Long> getDisksSpace(IMachine machine) {
        Map<String, Long> disks = new HashMap<>();
        for (IMediumAttachment attachment : machine.getMediumAttachments()) {
            if (isHardDisk(attachment)) {
                IMedium medium = attachment.getMedium();
                if (medium != null) {
                    disks.put(medium.getName(), medium.getSize());
                }
            }
        }
        return disks;
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

    public Map<String, Long> getDisksSpace() {
        return disksSpace;
    }

    public void setDisksSpace(Map<String, Long> disksSpace) {
        this.disksSpace = disksSpace;
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
        return String.format("Name: %s\nOS: %s\nRAM size: %d\nCPU's: %d", name, operationSystem, memorySize, cpuCount);
    }
}
