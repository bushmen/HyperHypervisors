package pl.edu.agh.student.hyperhypervisors.agent.vm;

import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.AppServerModel;

import java.util.Set;

/**
 * This MBean is used to store information about all known application servers for virtual machine on which this agent is running
 */

public interface VirtualMachineAgentMXBean {

    void registerApplicationServer(AppServerModel appServer);

    Set<AppServerModel> getApplicationServers();
}
