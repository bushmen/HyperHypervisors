package pl.edu.agh.student.hyperhypervisors.agent.vm;

import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.AppServerModel;

import java.util.HashSet;
import java.util.Set;

public class VirtualMachineAgent implements VirtualMachineAgentMXBean {

    private Set<AppServerModel> appServers = new HashSet<>();

    @Override
    public synchronized void registerApplicationServer(AppServerModel appServer) {
        if(!appServers.contains(appServer)) {
            appServers.add(appServer);
        }
    }

    @Override
    public synchronized Set<AppServerModel> getApplicationServers() {
        return appServers;
    }
}
