package pl.edu.agh.student.hyperhypervisors.web.services;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.security.access.AccessDeniedException;
import pl.edu.agh.student.hyperhypervisors.web.dto.HypervisorData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.VirtualMachine;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.HypervisorRepository;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class HypervisorServiceTests {

    private static final String NAME = "NAME";
    private static final String USER_NAME = "USER_NAME";
    private static final Long ID = 1L;
    private static final Integer PORT = 2;

    private HypervisorRepository hypervisorRepositoryMock;
    private UserService userServiceMock;
    private ServerService serverServiceMock;
    private VirtualMachineService virtualMachineServiceMock;
    private Neo4jOperations templateMock;
    private VirtualMachine virtualMachineMock;
    private User userMock;
    private ServerNode serverMock;
    private Hypervisor hypervisorMock;
    private AgentConnector agentConnectorMock;
    private HypervisorService testInstance;

    @Before
    public void setUp() throws Exception {
        hypervisorRepositoryMock = createMock(HypervisorRepository.class);
        userServiceMock = createMock(UserService.class);
        serverServiceMock = createMock(ServerService.class);
        virtualMachineServiceMock = createMock(VirtualMachineService.class);
        templateMock = createMock(Neo4jOperations.class);
        virtualMachineMock = createMock(VirtualMachine.class);
        userMock = createMock(User.class);
        serverMock = createMock(ServerNode.class);
        hypervisorMock = createMock(Hypervisor.class);
        agentConnectorMock = createMock(AgentConnector.class);

        testInstance = new HypervisorServiceMock(agentConnectorMock);
        testInstance.hypervisorRepository = hypervisorRepositoryMock;
        testInstance.userService = userServiceMock;
        testInstance.serverService = serverServiceMock;
        testInstance.virtualMachineService = virtualMachineServiceMock;
        testInstance.template = templateMock;
    }

    @Test
    public void testGetHypervisorsData() throws Exception {
        List<Hypervisor> hypervisors = Lists.newArrayList(hypervisorMock);

        expect(serverMock.getHypervisors()).andReturn(hypervisors).times(3);
        expect(templateMock.fetch(hypervisors)).andReturn(hypervisors).times(3);
        expect(agentConnectorMock.getHypervisors()).andReturn(hypervisors);
        expect(hypervisorMock.getPort()).andReturn(PORT).times(4);

        expect(virtualMachineServiceMock.getVirtualMachinesData(agentConnectorMock, hypervisorMock)).andReturn(null);
        expect(hypervisorMock.getId()).andReturn(ID);
        expect(hypervisorMock.getName()).andReturn(NAME);

        replay(templateMock, agentConnectorMock, hypervisorMock, serverMock, virtualMachineServiceMock);

        List<HypervisorData> hypervisorsData = testInstance.getHypervisorsData(agentConnectorMock, serverMock);
        verify(templateMock, agentConnectorMock, hypervisorMock, serverMock, virtualMachineServiceMock);

        assertEquals(1, hypervisorsData.size());
        assertEquals(hypervisorMock, hypervisorsData.get(0).getNode());
    }

    @Test
    public void testCreateHypervisor() throws Exception {
        expect(hypervisorRepositoryMock.save(hypervisorMock)).andReturn(hypervisorMock);
        expect(agentConnectorMock.getVirtualMachinesNames(hypervisorMock)).andReturn(Lists.newArrayList(NAME));
        expect(virtualMachineServiceMock.createVirtualMachine(NAME)).andReturn(virtualMachineMock);
        expect(hypervisorMock.getVirtualMachines()).andReturn(new ArrayList<VirtualMachine>());
        expect(hypervisorRepositoryMock.save(hypervisorMock)).andReturn(hypervisorMock);
        serverServiceMock.addHypervisor(serverMock, hypervisorMock);
        expectLastCall();

        replay(agentConnectorMock, hypervisorRepositoryMock, hypervisorMock,
                virtualMachineServiceMock, virtualMachineMock, serverServiceMock);

        Hypervisor hypervisor = testInstance.createHypervisor(hypervisorMock, serverMock);
        verify(agentConnectorMock, hypervisorRepositoryMock, hypervisorMock,
                virtualMachineServiceMock, virtualMachineMock, serverServiceMock);

        assertEquals(hypervisorMock, hypervisor);
    }

    @Test
    public void testGetHypervisorForVMWhenAllowed() throws Exception {
        Hypervisor hypervisor = testGetHypervisorForVM(Lists.newArrayList(virtualMachineMock));
        assertEquals(hypervisorMock, hypervisor);
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetHypervisorForVMWhenNotAllowed() throws Exception {
        testGetHypervisorForVM(new ArrayList<VirtualMachine>());
    }

    private Hypervisor testGetHypervisorForVM(List<VirtualMachine> vms) {
        getHypervisorForVMPrepare(vms);

        replay(serverMock, userServiceMock, userMock, templateMock, hypervisorRepositoryMock, hypervisorMock);

        Hypervisor hypervisor = testInstance.getHypervisorForVirtualMachineIfAllowed(USER_NAME, virtualMachineMock);

        verify(serverMock, userServiceMock, userMock, templateMock, hypervisorRepositoryMock, hypervisorMock);
        return hypervisor;
    }

    private void getHypervisorForVMPrepare(List<VirtualMachine> vms) {
        List<ServerNode> servers = Lists.newArrayList(serverMock);
        List<Hypervisor> hypervisors = Lists.newArrayList(hypervisorMock);

        expect(userServiceMock.findByLogin(USER_NAME)).andReturn(userMock);
        expect(userMock.getServers()).andReturn(servers);
        expect(templateMock.fetch(servers)).andReturn(servers);
        expect(serverMock.getHypervisors()).andReturn(hypervisors);
        expect(templateMock.fetch(hypervisors)).andReturn(hypervisors);
        expect(hypervisorMock.getVirtualMachines()).andReturn(vms);
        expect(templateMock.fetch(vms)).andReturn(vms);
    }

    @Test
    public void testAddVM() throws Exception {
        List<VirtualMachine> vms = new ArrayList<>();

        expect(hypervisorMock.getVirtualMachines()).andReturn(vms);
        expect(hypervisorRepositoryMock.save(hypervisorMock)).andReturn(hypervisorMock);

        replay(hypervisorRepositoryMock, hypervisorMock, virtualMachineMock);

        testInstance.addVirtualMachine(hypervisorMock, virtualMachineMock);
        verify(hypervisorRepositoryMock, hypervisorMock, virtualMachineMock);

        assertEquals(1, vms.size());
        assertEquals(virtualMachineMock, vms.get(0));
    }

    private static class HypervisorServiceMock extends HypervisorService {

        private AgentConnector agentConnector;

        public HypervisorServiceMock(AgentConnector agentConnector) {
            this.agentConnector = agentConnector;
        }

        @Override
        protected AgentConnector createAgentConnector(ServerNode serverNode) {
            return agentConnector;
        }
    }
}
