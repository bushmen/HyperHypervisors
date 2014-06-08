package pl.edu.agh.student.hyperhypervisors.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeLoginAndPassword;
import pl.edu.agh.student.hyperhypervisors.web.dto.HypervisorData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.VirtualMachine;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.HypervisorRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class HypervisorService {

    @Autowired
    HypervisorRepository hypervisorRepository;

    @Autowired
    UserService userService;

    @Autowired
    ServerService serverService;

    @Autowired
    VirtualMachineService virtualMachineService;

    @Autowired
    Neo4jOperations template;

    public List<HypervisorData> getHypervisorsData(AgentConnector agentConnector, ServerNode serverNode) throws Exception {
        Collection<Hypervisor> hypervisors = template.fetch(serverNode.getHypervisors());
        List<Hypervisor> runningHypervisors = agentConnector.getHypervisors();
        List<Integer> hypervisorsPortsInDb = new ArrayList<>();
        for(Hypervisor hypervisor: hypervisors) {
            hypervisorsPortsInDb.add(hypervisor.getPort());
        }

        for (Hypervisor hypervisor: runningHypervisors) {
            if (!hypervisorsPortsInDb.contains(hypervisor.getPort())) {
                createHypervisor(hypervisor, serverNode);
            }
        }
        hypervisors = template.fetch(serverNode.getHypervisors());

        List<Integer> runningHypervisorsPorts = new ArrayList<>();
        for (Hypervisor runningHypervisor : runningHypervisors) {
            runningHypervisorsPorts.add(runningHypervisor.getPort());
        }

        for (Hypervisor hypervisor : hypervisors) {
            if(!runningHypervisorsPorts.contains(hypervisor.getPort())) {
                hypervisorRepository.deleteWithSubtree(hypervisor);
            }
        }

        hypervisors = template.fetch(serverNode.getHypervisors());
        return getHypervisorsData(agentConnector, hypervisors);
    }

    private List<HypervisorData> getHypervisorsData(AgentConnector agentConnector,
                                                    Collection<Hypervisor> hypervisors) throws Exception {
        List<HypervisorData> hypervisorsData = new ArrayList<>();
        for (Hypervisor hypervisor : hypervisors) {
            HypervisorData hypervisorData = new HypervisorData();
            hypervisorData.setNode(hypervisor);
            hypervisorData.setChildren(virtualMachineService.getVirtualMachinesData(agentConnector, hypervisor));
            hypervisorsData.add(hypervisorData);
        }
        return hypervisorsData;
    }

    public Hypervisor createHypervisor(Hypervisor hypervisor, ServerNode serverNode) throws Exception {
        Hypervisor savedHypervisor = hypervisorRepository.save(hypervisor);
        serverService.addHypervisor(serverNode, savedHypervisor);

        List<String> vmNames = createAgentConnector(serverNode).getVirtualMachinesNames(hypervisor);
        if(vmNames != null) {
            for (String vmName : vmNames) {
                VirtualMachine savedVM = virtualMachineService.createVirtualMachine(vmName);
                savedHypervisor.getVirtualMachines().add(savedVM);
            }
            savedHypervisor = hypervisorRepository.save(savedHypervisor);
        }

        return savedHypervisor;
    }

    public Hypervisor getHypervisorIfAllowed(String userName, Long hypervisorId) {
        User user = userService.findByLogin(userName);
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());
        Hypervisor hypervisorNode = hypervisorRepository.findOne(hypervisorId);

        //TODO can do better
        boolean allowed = false;
        for (ServerNode serverNode : userServerNodes) {
            Collection<Hypervisor> serversHypervisors = template.fetch(serverNode.getHypervisors());
            if (serversHypervisors.contains(hypervisorNode)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new AccessDeniedException("User: " + userName + ", hypervisor: " + hypervisorId);
        }
        return hypervisorNode;
    }

    public Hypervisor getHypervisorForVirtualMachineIfAllowed(String userName, VirtualMachine virtualMachine) {
        User user = userService.findByLogin(userName);
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());

        //TODO can do better
        Hypervisor vmsHypervisor = null;
        for (ServerNode serverNode : userServerNodes) {
            Collection<Hypervisor> serversHypervisors = template.fetch(serverNode.getHypervisors());
            for (Hypervisor hypervisor : serversHypervisors) {
                Collection<VirtualMachine> virtualMachines = template.fetch(hypervisor.getVirtualMachines());
                if (virtualMachines.contains(virtualMachine)) {
                    vmsHypervisor = hypervisor;
                    break;
                }
            }
        }
        if (vmsHypervisor == null) {
            throw new AccessDeniedException("User: " + userName + ", virtualMachine: " + virtualMachine.getId());
        }
        return vmsHypervisor;
    }

    public void setLoginAndPassword(ChangeLoginAndPassword hypervisor, Long hypervisorId, String name) {
        Hypervisor hypervisorNode = getHypervisorIfAllowed(name, hypervisorId);
        hypervisorNode.setLogin(hypervisor.getLogin());
        hypervisorNode.setPassword(hypervisor.getPassword());
        hypervisorRepository.save(hypervisorNode);
    }

    public void addVirtualMachine(Hypervisor hypervisor, VirtualMachine virtualMachine) {
        hypervisor.getVirtualMachines().add(virtualMachine);
        hypervisorRepository.save(hypervisor);
    }

    protected AgentConnector createAgentConnector(ServerNode serverNode) {
        return new AgentConnector(serverNode);
    }
}
