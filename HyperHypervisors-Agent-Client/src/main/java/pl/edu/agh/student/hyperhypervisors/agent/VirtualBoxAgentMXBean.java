package pl.edu.agh.student.hyperhypervisors.agent;

import pl.edu.agh.student.hyperhypervisors.dto.VirtualMachineDescription;

import java.util.List;

/**
 * This agent uses Oracle VirtualBox SDK to access information about virtual machines managed by pointed hypervisor
 */

public interface VirtualBoxAgentMXBean {

    List<String> getMachinesNamesList(String url, String user, String password);

    VirtualMachineDescription getMachineDescription(String url, String user, String password, String machineName);
}
