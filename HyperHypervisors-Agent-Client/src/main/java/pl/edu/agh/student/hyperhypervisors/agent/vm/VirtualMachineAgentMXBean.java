package pl.edu.agh.student.hyperhypervisors.agent.vm;

import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.AppServerModel;

import java.util.Set;

public interface VirtualMachineAgentMXBean {

    void registerApplicationServer(AppServerModel appServer);

    Set<AppServerModel> getApplicationServers();
}
