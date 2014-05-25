package pl.edu.agh.student.hyperhypervisors.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.edu.agh.student.hyperhypervisors.model.VirtualMachineDescription;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAddressData;
import pl.edu.agh.student.hyperhypervisors.web.dto.VirtualMachineData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.*;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.VirtualMachineRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class VirtualMachineService {

    @Autowired
    VirtualMachineRepository virtualMachineRepository;

    @Autowired
    UserService userService;

    @Autowired
    HypervisorService hypervisorService;

    @Autowired
    ApplicationServerService applicationServerService;

    @Autowired
    Neo4jOperations template;

    public List<VirtualMachineData> getVirtualMachinesData(AgentConnector agentConnector, Hypervisor hypervisor) throws Exception {
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
            virtualMachineData.setChildren(applicationServerService.getApplicationServersData(agentConnector, virtualMachine));
            virtualMachinesData.add(virtualMachineData);
        }
        return virtualMachinesData;
    }

    public VirtualMachine createVirtualMachine(String vmName) {
        VirtualMachine vm = new VirtualMachine();
        vm.setName(vmName);
        return virtualMachineRepository.save(vm);
    }

    public VirtualMachine getVirtualMachineIfAllowed(String userName, Long vmId) {
        VirtualMachine virtualMachine = virtualMachineRepository.findOne(vmId);
        User user = userService.findByLogin(userName);
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());

        //TODO can do better
        boolean allowed = false;
        for (ServerNode serverNode : userServerNodes) {
            Collection<Hypervisor> serversHypervisors = template.fetch(serverNode.getHypervisors());
            for (Hypervisor hypervisor : serversHypervisors) {
                Collection<VirtualMachine> hypervisorsVMs = template.fetch(hypervisor.getVirtualMachines());
                if (hypervisorsVMs.contains(virtualMachine)) {
                    allowed = true;
                    break;
                }
            }
        }
        if (!allowed) {
            throw new AccessDeniedException("User: " + userName + ", virtualMachine: " + vmId);
        }
        return virtualMachine;
    }

    public void setIPAddress(ChangeIpAddressData vmData, Long vmId, Principal principal) {
        VirtualMachine virtualMachine = getVirtualMachineIfAllowed(principal.getName(), vmId);
        Hypervisor vmHypervisor = hypervisorService.getHypervisorForVirtualMachineIfAllowed(principal.getName(), virtualMachine);
        virtualMachine.setIpAddress(vmData.getIpAddress());
        VirtualMachine savedVirtualMachine = virtualMachineRepository.save(virtualMachine);
        hypervisorService.addVirtualMachine(vmHypervisor, savedVirtualMachine);
    }

    public void addApplicationServer(VirtualMachine virtualMachine, ApplicationServer appServer) {
        virtualMachine.getApplicationServers().add(appServer);
        virtualMachineRepository.save(virtualMachine);
    }
}
