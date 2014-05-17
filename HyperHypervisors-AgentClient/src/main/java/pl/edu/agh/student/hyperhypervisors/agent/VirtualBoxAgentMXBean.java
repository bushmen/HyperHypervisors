package pl.edu.agh.student.hyperhypervisors.agent;

import pl.edu.agh.student.hyperhypervisors.model.VirtualMachineDescription;

import java.util.List;

public interface VirtualBoxAgentMXBean {

    List<String> getMachinesNamesList(String url, String user, String password);

    VirtualMachineDescription getMachineDescription(String url, String user, String password, String machineName);
}
