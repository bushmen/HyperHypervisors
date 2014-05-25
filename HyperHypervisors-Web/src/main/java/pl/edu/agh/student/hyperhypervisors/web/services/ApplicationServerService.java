package pl.edu.agh.student.hyperhypervisors.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.edu.agh.student.hyperhypervisors.web.dto.ApplicationServerData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangePortData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.*;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.ApplicationServerRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class ApplicationServerService {

    @Autowired
    ApplicationServerRepository applicationServerRepository;

    @Autowired
    UserService userService;

    @Autowired
    VirtualMachineService virtualMachineService;

    @Autowired
    Neo4jOperations template;

    public List<ApplicationServerData> getApplicationServersData(AgentConnector agentConnector,
                                                                 VirtualMachine virtualMachine) throws Exception {
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

    public ApplicationServer createApplicationServer(ApplicationServer appServer, Long vmId, String userName) {
        VirtualMachine virtualMachine = virtualMachineService.getVirtualMachineIfAllowed(userName, vmId);
        ApplicationServer savedAppServer = applicationServerRepository.save(appServer);
        virtualMachineService.addApplicationServer(virtualMachine, savedAppServer);
        return savedAppServer;
    }

    public ApplicationServer getApplicationServerIfAllowed(String userName, Long appServerId) {
        User user = userService.findByLogin(userName);
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());
        ApplicationServer appServerNode = applicationServerRepository.findOne(appServerId);

        //TODO can do better
        boolean allowed = false;
        for (ServerNode serverNode : userServerNodes) {
            Collection<Hypervisor> serversHypervisors = template.fetch(serverNode.getHypervisors());
            for (Hypervisor hypervisor : serversHypervisors) {
                Collection<VirtualMachine> hypervisorsVMs = template.fetch(hypervisor.getVirtualMachines());
                for (VirtualMachine vm : hypervisorsVMs) {
                    Collection<ApplicationServer> applicationServers = template.fetch(vm.getApplicationServers());
                    if (applicationServers.contains(appServerNode)) {
                        allowed = true;
                        break;
                    }
                }
            }
        }
        if (!allowed) {
            throw new AccessDeniedException("User: " + userName + ", appServer: " + appServerId);
        }
        return appServerNode;
    }

    public void setJmxPort(ChangePortData appServer, Long appServerId, String userName) {
        ApplicationServer appServerNode = getApplicationServerIfAllowed(userName, appServerId);
        appServerNode.setJmxPort(appServer.getPort());
        applicationServerRepository.save(appServerNode);
    }

    public void removeApplicationServer(Long appServerId, String userName) {
        ApplicationServer appServerNode = getApplicationServerIfAllowed(userName, appServerId);
        applicationServerRepository.deleteWithSubtree(appServerNode);
    }
}
