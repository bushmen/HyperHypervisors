package pl.edu.agh.student.hyperhypervisors.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Service;
import pl.edu.agh.student.hyperhypervisors.web.dto.ApplicationServerData;
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
    VirtualMachineService virtualMachineService;

    @Autowired
    Neo4jOperations template;

    public List<ApplicationServerData> getApplicationServersData(AgentConnector agentConnector,
                                                                 VirtualMachine virtualMachine) throws Exception {
        Collection<ApplicationServer> applicationServers = template.fetch(virtualMachine.getApplicationServers());
        List<ApplicationServer> appServers = agentConnector.getApplicationServers(virtualMachine);
        List<Integer> appServersPortsInDb = new ArrayList<>();
        for(ApplicationServer appServer: applicationServers) {
            appServersPortsInDb.add(appServer.getJmxPort());
        }

        if(appServers != null) {
            for (ApplicationServer server : appServers) {
                if (!appServersPortsInDb.contains(server.getJmxPort())) {
                    ApplicationServer savedAppServer = createApplicationServer(server, virtualMachine.getId());
                    virtualMachine.getApplicationServers().add(savedAppServer);
                }
            }
        }

        applicationServers = template.fetch(virtualMachine.getApplicationServers());
        return getAppServersData(agentConnector, virtualMachine, applicationServers);
    }

    private List<ApplicationServerData> getAppServersData(AgentConnector agentConnector, VirtualMachine virtualMachine,
                                                          Collection<ApplicationServer> applicationServers) throws Exception {
        List<ApplicationServerData> applicationServersData = new ArrayList<>();
        for (ApplicationServer applicationServer : applicationServers) {
            ApplicationServerData applicationServerData = new ApplicationServerData();

            List<Application> applications = new ArrayList<>();
            List<String> appsNames = agentConnector.getApplicationsNames(virtualMachine, applicationServer);
            if(appsNames == null) {
                applicationServerRepository.deleteWithSubtree(applicationServer);
                continue;
            }

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

    public ApplicationServer createApplicationServer(ApplicationServer appServer, Long vmId) {
        VirtualMachine virtualMachine = virtualMachineService.getVirtualMachine(vmId);
        ApplicationServer savedAppServer = applicationServerRepository.save(appServer);
        virtualMachineService.addApplicationServer(virtualMachine, savedAppServer);
        return savedAppServer;
    }
}
