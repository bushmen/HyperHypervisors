package pl.edu.agh.student.hyperhypervisors.agent;

import pl.edu.agh.student.hyperhypervisors.model.MachineDescription;

import java.util.List;

public interface VirtualBoxAgentMXBean {

    List<String> getMachinesNamesList();
    MachineDescription getMachineDescription(String machineName);
}
