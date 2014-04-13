package pl.edu.agh.student.hyperhypervisors.model;

public class MachineDescription {
    private String name;
    private String operationSystem;
    private long memorySize;
    private long cpuCount;
    private long diskSpace;

    public MachineDescription(){

    }

    public MachineDescription(String name, String operationSystem, long memorySize, long cpuCount, long diskSpace){
        this.name = name;
        this.operationSystem = operationSystem;
        this.memorySize = memorySize;
        this.cpuCount = cpuCount;
        this.diskSpace = diskSpace;
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

    public long getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(long diskSpace) {
        this.diskSpace = diskSpace;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nOS: " + operationSystem + "\nHard disk size: " + diskSpace + "KB\nRAM size: " + memorySize + "\nCPU's: " + cpuCount;
    }
}
