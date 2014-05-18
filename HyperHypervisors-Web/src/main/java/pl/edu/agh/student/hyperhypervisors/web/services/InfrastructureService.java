package pl.edu.agh.student.hyperhypervisors.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Service;
import pl.edu.agh.student.hyperhypervisors.model.ServerDescription;
import pl.edu.agh.student.hyperhypervisors.model.VirtualMachineDescription;
import pl.edu.agh.student.hyperhypervisors.web.dto.ApplicationServerData;
import pl.edu.agh.student.hyperhypervisors.web.dto.HypervisorData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ServerData;
import pl.edu.agh.student.hyperhypervisors.web.dto.VirtualMachineData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.*;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.ApplicationServerRepository;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.HypervisorRepository;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.VirtualMachineRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class InfrastructureService {

    @Autowired
    HypervisorRepository hypervisorRepository;

    @Autowired
    VirtualMachineRepository virtualMachineRepository;

    @Autowired
    ApplicationServerRepository applicationServerRepository;

    @Autowired
    Neo4jOperations template;

    public List<ServerData> getServersData(User user) throws Exception {
        List<ServerData> data = new ArrayList<>();
        Collection<ServerNode> serverNodes = template.fetch(user.getServers());
        for (ServerNode serverNode : serverNodes) {
            ServerData serverData = new ServerData();
            serverData.setNode(serverNode);

            AgentConnector agentConnector = new AgentConnector(serverNode);
            ServerDescription serverDescription = agentConnector.getServerDescription();
            serverData.setDescription(serverDescription);
            serverData.setChildren(getHypervisorsData(agentConnector, serverNode));
            data.add(serverData);
        }
        return data;
    }

    private List<HypervisorData> getHypervisorsData(AgentConnector agentConnector, ServerNode serverNode) throws Exception {
        Collection<Hypervisor> hypervisors = template.fetch(serverNode.getHypervisors());
        List<HypervisorData> hypervisorsData = new ArrayList<>();
        for (Hypervisor hypervisor : hypervisors) {
            HypervisorData hypervisorData = new HypervisorData();
            hypervisorData.setNode(hypervisor);
            hypervisorData.setChildren(getVirtualMachinesData(agentConnector, hypervisor));
            hypervisorsData.add(hypervisorData);
        }
        return hypervisorsData;
    }

    private List<VirtualMachineData> getVirtualMachinesData(AgentConnector agentConnector, Hypervisor hypervisor) throws Exception {
        Collection<VirtualMachine> virtualMachines = template.fetch(hypervisor.getVirtualMachines());
        List<String> vmNames = agentConnector.getVirtualMachinesNames(hypervisor);
        List<String> vmNamesInDb = new ArrayList<>();
        for (VirtualMachine virtualMachine : virtualMachines) {
            vmNamesInDb.add(virtualMachine.getName());
        }

        for (String name : vmNames) {
            if (!vmNamesInDb.contains(name)) {
                VirtualMachine vm = new VirtualMachine();
                vm.setName(name);
                VirtualMachine savedVM = virtualMachineRepository.save(vm);
                hypervisor.getVirtualMachines().add(savedVM);
            }
        }

        virtualMachines = template.fetch(hypervisor.getVirtualMachines());
        return getVirtualMachinesDescriptions(agentConnector, hypervisor, virtualMachines);
    }

    private List<VirtualMachineData> getVirtualMachinesDescriptions(AgentConnector agentConnector, Hypervisor hypervisor,
                                                                    Collection<VirtualMachine> virtualMachines) throws Exception {
        List<VirtualMachineData> virtualMachinesData = new ArrayList<>();
        for (VirtualMachine virtualMachine : virtualMachines) {
            VirtualMachineData virtualMachineData = new VirtualMachineData();
            virtualMachineData.setNode(virtualMachine);

            VirtualMachineDescription vmDescription = agentConnector.getVirtualMachineDescription(hypervisor, virtualMachine.getName());
            if (vmDescription.getName() == null) {
                hypervisor.getVirtualMachines().remove(virtualMachine);
                virtualMachineRepository.deleteWithSubtree(virtualMachine);
                continue;
            }
            virtualMachineData.setDescription(vmDescription);
            virtualMachineData.setChildren(getApplicationServersData(agentConnector, virtualMachine));
            virtualMachinesData.add(virtualMachineData);
        }
        return virtualMachinesData;
    }

    private List<ApplicationServerData> getApplicationServersData(AgentConnector agentConnector, VirtualMachine virtualMachine) throws Exception {
        Collection<ApplicationServer> applicationServers = template.fetch(virtualMachine.getApplicationServers());
        List<ApplicationServerData> applicationServersData = new ArrayList<>();
        for (ApplicationServer applicationServer : applicationServers) {
            ApplicationServerData applicationServerData = new ApplicationServerData();

            List<Application> applications = new ArrayList<>();
            List<String> appsNames = agentConnector.getApplicationsNames(virtualMachine, applicationServer);
            Collections.sort(appsNames);
            for (String appName : appsNames) {
                Application app = new Application();
                app.setName(appName);
                applications.add(app);
            }
            applicationServer.setApplications(applications);
            applicationServerData.setNode(applicationServer);
            applicationServersData.add(applicationServerData);
        }
        return applicationServersData;
    }
}
