package pl.edu.agh.student.hyperhypervisors.web.services;

import com.google.common.collect.Lists;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import pl.edu.agh.student.hyperhypervisors.web.dto.ApplicationServerData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Application;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ApplicationServer;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.VirtualMachine;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.ApplicationServerRepository;

import java.util.Collection;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class ApplicationServerServiceTests {

    private static final String APP_NAME = "APP_NAME";
    private static final Long ID = 1L;
    private static final Integer PORT = 2;

    private Neo4jTemplate templateMock;
    private ApplicationServerRepository applicationServerRepositoryMock;
    private VirtualMachineService virtualMachineServiceMock;
    private VirtualMachine virtualMachineMock;
    private ApplicationServer applicationServerMock;
    private ApplicationServerService testInstance;

    @Before
    public void setUp() throws Exception {
        templateMock = createMock(Neo4jTemplate.class);
        applicationServerRepositoryMock = createMock(ApplicationServerRepository.class);
        virtualMachineServiceMock = createMock(VirtualMachineService.class);
        virtualMachineMock = createMock(VirtualMachine.class);
        applicationServerMock = createMock(ApplicationServer.class);

        testInstance = new ApplicationServerService();
        testInstance.template = templateMock;
        testInstance.applicationServerRepository = applicationServerRepositoryMock;
        testInstance.virtualMachineService = virtualMachineServiceMock;
    }

    @Test
    public void testGetAppServersData() throws Exception {
        AgentConnector agentConnectorMock = createMock(AgentConnector.class);

        List<ApplicationServer> appServers = Lists.newArrayList(applicationServerMock);

        expect(virtualMachineMock.getApplicationServers()).andReturn(appServers).times(2);
        expect(templateMock.fetch(appServers)).andReturn(appServers).times(2);
        expect(agentConnectorMock.getApplicationServers(virtualMachineMock)).andReturn(appServers);
        expect(applicationServerMock.getJmxPort()).andReturn(PORT).times(2);

        expect(agentConnectorMock.getApplicationsNames(virtualMachineMock, applicationServerMock))
                .andReturn(Lists.newArrayList(APP_NAME));
        applicationServerMock.setApplications(EasyMock.<Collection<Application>>anyObject());
        expectLastCall();
        expect(applicationServerMock.getId()).andReturn(ID);
        expect(applicationServerMock.getName()).andReturn(APP_NAME);

        replay(templateMock, agentConnectorMock, virtualMachineMock, applicationServerMock);

        List<ApplicationServerData> appServersData = testInstance.getApplicationServersData(agentConnectorMock, virtualMachineMock);
        verify(templateMock, agentConnectorMock, virtualMachineMock, applicationServerMock);

        assertEquals(1, appServersData.size());
        assertEquals(applicationServerMock, appServersData.get(0).getNode());
    }

    @Test
    public void testCreateAppServer() throws Exception {
        expect(virtualMachineServiceMock.getVirtualMachine(ID)).andReturn(virtualMachineMock);
        expect(applicationServerRepositoryMock.save(applicationServerMock)).andReturn(applicationServerMock);
        virtualMachineServiceMock.addApplicationServer(virtualMachineMock, applicationServerMock);
        expectLastCall();

        replay(virtualMachineServiceMock, virtualMachineMock, applicationServerRepositoryMock, applicationServerMock);

        ApplicationServer applicationServer = testInstance.createApplicationServer(applicationServerMock, ID);
        verify(virtualMachineServiceMock, virtualMachineMock, applicationServerRepositoryMock, applicationServerMock);

        assertEquals(applicationServerMock, applicationServer);
    }
}
