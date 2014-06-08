package pl.edu.agh.student.hyperhypervisors.web.services;

import com.google.common.collect.Lists;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.security.access.AccessDeniedException;
import pl.edu.agh.student.hyperhypervisors.dto.VirtualMachineDescription;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAndPortData;
import pl.edu.agh.student.hyperhypervisors.web.dto.VirtualMachineData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.*;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.VirtualMachineRepository;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class VirtualMachineServiceTests {

    private static final String NAME = "NAME";
    private static final String NAME2 = "NAME2";
    private static final String USER_NAME = "USER_NAME";
    private static final Long ID = 1L;
    private static final Long ID2 = 2L;
    private static final String IP = "1.1.1.1";
    private static final int PORT = 1234;

    private VirtualMachineRepository virtualMachineRepositoryMock;
    private UserService userServiceMock;
    private HypervisorService hypervisorServiceMock;
    private ApplicationServerService applicationServerServiceMock;
    private Neo4jOperations templateMock;
    private VirtualMachine virtualMachineMock;
    private User userMock;
    private ServerNode serverMock;
    private Hypervisor hypervisorMock;
    private AgentConnector agentConnectorMock;
    private ApplicationServer applicationServerMock;
    private VirtualMachineService testInstance;

    @Before
    public void setUp() throws Exception {
        virtualMachineRepositoryMock = createMock(VirtualMachineRepository.class);
        userServiceMock = createMock(UserService.class);
        hypervisorServiceMock = createMock(HypervisorService.class);
        applicationServerServiceMock = createMock(ApplicationServerService.class);
        templateMock = createMock(Neo4jOperations.class);
        virtualMachineMock = createMock(VirtualMachine.class);
        userMock = createMock(User.class);
        serverMock = createMock(ServerNode.class);
        hypervisorMock = createMock(Hypervisor.class);
        agentConnectorMock = createMock(AgentConnector.class);
        applicationServerMock = createMock(ApplicationServer.class);

        testInstance = new VirtualMachineService();
        testInstance.virtualMachineRepository = virtualMachineRepositoryMock;
        testInstance.userService = userServiceMock;
        testInstance.hypervisorService = hypervisorServiceMock;
        testInstance.applicationServerService = applicationServerServiceMock;
        testInstance.template = templateMock;
    }

    @Test
    public void testGetVMsData() throws Exception {
        VirtualMachine secondVMMock = createMock(VirtualMachine.class);
        VirtualMachineDescription vmDescription1Mock = createMock(VirtualMachineDescription.class);
        VirtualMachineDescription vmDescription2Mock = createMock(VirtualMachineDescription.class);

        List<VirtualMachine> vms = Lists.newArrayList(virtualMachineMock);

        expect(hypervisorMock.getVirtualMachines()).andReturn(vms).times(2);
        expect(agentConnectorMock.getVirtualMachinesNames(hypervisorMock)).andReturn(Lists.newArrayList(NAME2));
        expect(templateMock.fetch(vms)).andReturn(vms).times(2);

        expect(virtualMachineMock.getId()).andReturn(ID);
        expect(virtualMachineMock.getName()).andReturn(NAME).times(3);

        expect(agentConnectorMock.getVirtualMachineDescription(hypervisorMock, NAME)).andReturn(vmDescription1Mock);
        expect(vmDescription1Mock.getName()).andReturn(null);

        expect(virtualMachineRepositoryMock.save(EasyMock.<VirtualMachine>anyObject())).andReturn(secondVMMock);
        virtualMachineRepositoryMock.deleteWithSubtree(virtualMachineMock);
        expectLastCall();

        replay(virtualMachineMock, virtualMachineRepositoryMock, hypervisorMock, templateMock,
                agentConnectorMock, applicationServerServiceMock, secondVMMock, vmDescription1Mock, vmDescription2Mock);

        List<VirtualMachineData> vmsData = testInstance.getVirtualMachinesData(agentConnectorMock, hypervisorMock);
        verify(virtualMachineMock, virtualMachineRepositoryMock, hypervisorMock, templateMock,
                agentConnectorMock, applicationServerServiceMock, secondVMMock, vmDescription1Mock, vmDescription2Mock);

        assertEquals(0, vmsData.size());
    }

    @Test
    public void testCreateVM() throws Exception {
        expect(virtualMachineRepositoryMock.save(EasyMock.<VirtualMachine>anyObject())).andReturn(virtualMachineMock);

        replay(virtualMachineRepositoryMock, virtualMachineMock);

        VirtualMachine vm = testInstance.createVirtualMachine(NAME);
        verify(virtualMachineRepositoryMock, virtualMachineMock);

        assertEquals(vm, virtualMachineMock);
    }

    @Test
    public void testGetAppServerWhenAllowed() throws Exception {
        VirtualMachine vm = testGetVM(Lists.newArrayList(virtualMachineMock));
        assertEquals(vm, virtualMachineMock);
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetAppServerWhenNotAllowed() throws Exception {
        testGetVM(new ArrayList<VirtualMachine>());
    }

    private VirtualMachine testGetVM(List<VirtualMachine> vms) {
        getVMPrepare(vms);

        replay(virtualMachineMock, userServiceMock, templateMock,
                virtualMachineRepositoryMock, userMock, serverMock, hypervisorMock);

        VirtualMachine vm = testInstance.getVirtualMachineIfAllowed(USER_NAME, ID);

        verify(virtualMachineMock, userServiceMock, templateMock,
                virtualMachineRepositoryMock, userMock, serverMock, hypervisorMock);
        return vm;
    }

    @Test
    public void testSetIPAndPortIfAllowed() throws Exception {
        testSetIPAndPort(Lists.newArrayList(virtualMachineMock));
    }

    @Test(expected = AccessDeniedException.class)
    public void testSetIPAndPortIfNotAllowed() throws Exception {
        testSetIPAndPort(new ArrayList<VirtualMachine>());
    }

    private void testSetIPAndPort(ArrayList<VirtualMachine> vms) {
        getVMPrepare(vms);

        ChangeIpAndPortData changeIpAddressData = createMock(ChangeIpAndPortData.class);
        expect(hypervisorServiceMock.getHypervisorForVirtualMachineIfAllowed(USER_NAME, virtualMachineMock))
                .andReturn(hypervisorMock);
        expect(changeIpAddressData.getIpAddress()).andReturn(IP);
        expect(changeIpAddressData.getPort()).andReturn(PORT);
        virtualMachineMock.setIpAddress(IP);
        expectLastCall();
        virtualMachineMock.setAgentPort(PORT);
        expectLastCall();
        expect(virtualMachineRepositoryMock.save(virtualMachineMock)).andReturn(virtualMachineMock);
        hypervisorServiceMock.addVirtualMachine(hypervisorMock, virtualMachineMock);
        expectLastCall();

        replay(virtualMachineMock, userServiceMock, templateMock, virtualMachineRepositoryMock,
                userMock, serverMock, hypervisorServiceMock, hypervisorMock, changeIpAddressData);

        testInstance.setIPAddressAndPort(changeIpAddressData, ID, USER_NAME);

        verify(virtualMachineMock, userServiceMock, templateMock, virtualMachineRepositoryMock,
                userMock, serverMock, hypervisorServiceMock, hypervisorMock, changeIpAddressData);
    }

    private void getVMPrepare(List<VirtualMachine> vms) {
        List<ServerNode> servers = Lists.newArrayList(serverMock);
        List<Hypervisor> hypervisors = Lists.newArrayList(hypervisorMock);

        expect(virtualMachineRepositoryMock.findOne(ID)).andReturn(virtualMachineMock);
        expect(userServiceMock.findByLogin(USER_NAME)).andReturn(userMock);
        expect(userMock.getServers()).andReturn(servers);
        expect(templateMock.fetch(servers)).andReturn(servers);
        expect(serverMock.getHypervisors()).andReturn(hypervisors);
        expect(templateMock.fetch(hypervisors)).andReturn(hypervisors);
        expect(hypervisorMock.getVirtualMachines()).andReturn(vms);
        expect(templateMock.fetch(vms)).andReturn(vms);
    }

    @Test
    public void testAddAppServer() throws Exception {
        List<ApplicationServer> applicationServers = new ArrayList<>();

        expect(virtualMachineMock.getApplicationServers()).andReturn(applicationServers);
        expect(virtualMachineRepositoryMock.save(virtualMachineMock)).andReturn(virtualMachineMock);

        replay(virtualMachineRepositoryMock, virtualMachineMock, applicationServerMock);

        testInstance.addApplicationServer(virtualMachineMock, applicationServerMock);
        verify(virtualMachineRepositoryMock, virtualMachineMock, applicationServerMock);

        assertEquals(1, applicationServers.size());
        assertEquals(applicationServerMock, applicationServers.get(0));
    }
}
