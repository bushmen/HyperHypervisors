package pl.edu.agh.student.hyperhypervisors.web.services;

import com.google.common.collect.Lists;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.security.access.AccessDeniedException;
import pl.edu.agh.student.hyperhypervisors.web.dto.ApplicationServerData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangePortData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.*;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.ApplicationServerRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class ApplicationServerServiceTests {

    private static final String APP_NAME = "APP_NAME";
    private static final String USER_NAME = "USER_NAME";
    private static final Long ID = 1L;
    private static final Integer PORT = 2;

    private Neo4jTemplate templateMock;
    private ApplicationServerRepository applicationServerRepositoryMock;
    private UserService userServiceMock;
    private VirtualMachineService virtualMachineServiceMock;
    private VirtualMachine virtualMachineMock;
    private ApplicationServer applicationServerMock;
    private User userMock;
    private ServerNode serverMock;
    private Hypervisor hypervisorMock;
    private ApplicationServerService testInstance;

    @Before
    public void setUp() throws Exception {
        templateMock = createMock(Neo4jTemplate.class);
        applicationServerRepositoryMock = createMock(ApplicationServerRepository.class);
        userServiceMock = createMock(UserService.class);
        virtualMachineServiceMock = createMock(VirtualMachineService.class);
        virtualMachineMock = createMock(VirtualMachine.class);
        applicationServerMock = createMock(ApplicationServer.class);
        userMock = createMock(User.class);
        serverMock = createMock(ServerNode.class);
        hypervisorMock = createMock(Hypervisor.class);

        testInstance = new ApplicationServerService();
        testInstance.template = templateMock;
        testInstance.applicationServerRepository = applicationServerRepositoryMock;
        testInstance.userService = userServiceMock;
        testInstance.virtualMachineService = virtualMachineServiceMock;
    }

    @Test
    public void testGetAppServersData() throws Exception {
        AgentConnector agentConnectorMock = createMock(AgentConnector.class);

        List<ApplicationServer> appServers = Lists.newArrayList(applicationServerMock);

        expect(virtualMachineMock.getApplicationServers()).andReturn(appServers);
        expect(templateMock.fetch(appServers)).andReturn(appServers);
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
        expect(virtualMachineServiceMock.getVirtualMachineIfAllowed(USER_NAME, ID)).andReturn(virtualMachineMock);
        expect(applicationServerRepositoryMock.save(applicationServerMock)).andReturn(applicationServerMock);
        virtualMachineServiceMock.addApplicationServer(virtualMachineMock, applicationServerMock);
        expectLastCall();

        replay(virtualMachineServiceMock, virtualMachineMock, applicationServerRepositoryMock, applicationServerMock);

        ApplicationServer applicationServer = testInstance.createApplicationServer(applicationServerMock, ID, USER_NAME);
        verify(virtualMachineServiceMock, virtualMachineMock, applicationServerRepositoryMock, applicationServerMock);

        assertEquals(applicationServerMock, applicationServer);
    }

    @Test
    public void testGetAppServerWhenAllowed() throws Exception {
        ApplicationServer applicationServer = testGetAppServer(Lists.newArrayList(applicationServerMock));
        assertEquals(applicationServer, applicationServerMock);
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetAppServerWhenNotAllowed() throws Exception {
        testGetAppServer(new ArrayList<ApplicationServer>());
    }

    private ApplicationServer testGetAppServer(ArrayList<ApplicationServer> appServers) {
        getAppServerPrepare(appServers);

        replay(virtualMachineMock, applicationServerMock, userServiceMock, templateMock,
                applicationServerRepositoryMock, userMock, serverMock, hypervisorMock);

        ApplicationServer applicationServer = testInstance.getApplicationServerIfAllowed(USER_NAME, ID);

        verify(virtualMachineMock, applicationServerMock, userServiceMock, templateMock,
                applicationServerRepositoryMock, userMock, serverMock, hypervisorMock);
        return applicationServer;
    }

    @Test
    public void testSetJmxPortIfAllowed() throws Exception {
        testSetJmxPort(Lists.newArrayList(applicationServerMock));
    }

    @Test(expected = AccessDeniedException.class)
    public void testSetJmxPortIfNotAllowed() throws Exception {
        testSetJmxPort(new ArrayList<ApplicationServer>());
    }

    private void testSetJmxPort(ArrayList<ApplicationServer> appServers) {
        getAppServerPrepare(appServers);

        ChangePortData changePortDataMock = createMock(ChangePortData.class);
        expect(changePortDataMock.getPort()).andReturn(PORT);
        applicationServerMock.setJmxPort(PORT);
        expectLastCall();
        expect(applicationServerRepositoryMock.save(applicationServerMock)).andReturn(applicationServerMock);

        replay(virtualMachineMock, applicationServerMock, userServiceMock, templateMock,
                applicationServerRepositoryMock, userMock, serverMock, hypervisorMock, changePortDataMock);

        testInstance.setJmxPort(changePortDataMock, ID, USER_NAME);

        verify(virtualMachineMock, applicationServerMock, userServiceMock, templateMock,
                applicationServerRepositoryMock, userMock, serverMock, hypervisorMock, changePortDataMock);
    }

    @Test
    public void testRemoveAppServerIfAllowed() throws Exception {
        testRemoveAppServer(Lists.newArrayList(applicationServerMock));
    }

    @Test(expected = AccessDeniedException.class)
    public void testRemoveAppServerIfNotAllowed() throws Exception {
        testRemoveAppServer(new ArrayList<ApplicationServer>());
    }

    private void testRemoveAppServer(ArrayList<ApplicationServer> appServers) {
        getAppServerPrepare(appServers);

        applicationServerRepositoryMock.deleteWithSubtree(applicationServerMock);
        expectLastCall();

        replay(virtualMachineMock, applicationServerMock, userServiceMock, templateMock,
                applicationServerRepositoryMock, userMock, serverMock, hypervisorMock);

        testInstance.removeApplicationServer(ID, USER_NAME);

        verify(virtualMachineMock, applicationServerMock, userServiceMock, templateMock,
                applicationServerRepositoryMock, userMock, serverMock, hypervisorMock);
    }

    private void getAppServerPrepare(ArrayList<ApplicationServer> appServers) {
        List<ServerNode> servers = Lists.newArrayList(serverMock);
        List<Hypervisor> hypervisors = Lists.newArrayList(hypervisorMock);
        List<VirtualMachine> vms = Lists.newArrayList(virtualMachineMock);

        expect(userServiceMock.findByLogin(USER_NAME)).andReturn(userMock);
        expect(userMock.getServers()).andReturn(servers);
        expect(templateMock.fetch(servers)).andReturn(servers);
        expect(applicationServerRepositoryMock.findOne(ID)).andReturn(applicationServerMock);
        expect(serverMock.getHypervisors()).andReturn(hypervisors);
        expect(templateMock.fetch(hypervisors)).andReturn(hypervisors);
        expect(hypervisorMock.getVirtualMachines()).andReturn(vms);
        expect(templateMock.fetch(vms)).andReturn(vms);
        expect(virtualMachineMock.getApplicationServers()).andReturn(appServers);
        expect(templateMock.fetch(appServers)).andReturn(appServers);
    }
}
