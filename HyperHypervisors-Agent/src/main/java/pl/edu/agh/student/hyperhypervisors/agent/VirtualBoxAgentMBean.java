package pl.edu.agh.student.hyperhypervisors.agent;

import java.util.List;

public interface VirtualBoxAgentMBean {

    List<String> getMachinesNamesList();
    public String getMachineName(final String machineName);
    public String getMachineOperationSystem(final String machineName);
    public long getMachineMemorySize(final String machineName);
    public long getMachineCPUCount(final String machineName);
    public long getMachineDiskSpace(final String machineName);
}
