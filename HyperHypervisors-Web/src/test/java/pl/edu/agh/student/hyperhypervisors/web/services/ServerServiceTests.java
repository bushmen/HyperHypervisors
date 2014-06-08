package pl.edu.agh.student.hyperhypervisors.web.services;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.security.access.AccessDeniedException;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAndPortData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ServerData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.ServerNodeRepository;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class ServerServiceTests {

    private static final String NAME = "NAME";
    private static final String USER_NAME = "USER_NAME";
    private static final Long ID = 1L;
    private static final Integer PORT = 2;
    private static final String IP = "1.1.1.1";

    private ServerNodeRepository serverRepositoryMock;
    private HypervisorService hypervisorServiceMock;
    private UserService userServiceMock;
    private Neo4jOperations templateMock;
    private User userMock;
    private ServerNode serverMock;
    private AgentConnector agentConnectorMock;
    private Hypervisor hypervisorMock;
    private ServerService testInstance;

    @Before
    public void setUp() throws Exception {
        hypervisorServiceMock = createMock(HypervisorService.class);
        userServiceMock = createMock(UserService.class);
        serverRepositoryMock = createMock(ServerNodeRepository.class);
        templateMock = createMock(Neo4jOperations.class);
        userMock = createMock(User.class);
        serverMock = createMock(ServerNode.class);
        agentConnectorMock = createMock(AgentConnector.class);
        hypervisorMock = createMock(Hypervisor.class);

        testInstance = new ServerServiceMock(agentConnectorMock);
        testInstance.serverNodeRepository = serverRepositoryMock;
        testInstance.hypervisorService = hypervisorServiceMock;
        testInstance.userService = userServiceMock;
        testInstance.template = templateMock;
    }

    @Test
    public void testGetServersData() throws Exception {
        List<ServerNode> servers = Lists.newArrayList(serverMock);

        expect(userServiceMock.findByLogin(USER_NAME)).andReturn(userMock);
        expect(userMock.getServers()).andReturn(servers);
        expect(templateMock.fetch(servers)).andReturn(servers);
        expect(agentConnectorMock.getServerDescription()).andReturn(null);
        expect(hypervisorServiceMock.getHypervisorsData(agentConnectorMock, serverMock)).andReturn(null);
        expect(serverMock.getId()).andReturn(ID);
        expect(serverMock.getName()).andReturn(NAME);

        replay(userServiceMock, userMock, templateMock, agentConnectorMock, hypervisorServiceMock, serverMock);

        List<ServerData> serversData = testInstance.getServersData(USER_NAME);
        verify(userServiceMock, userMock, templateMock, agentConnectorMock, hypervisorServiceMock, serverMock);

        assertEquals(1, serversData.size());
        assertEquals(serverMock, serversData.get(0).getNode());
    }

    @Test
    public void testCreateServer() throws Exception {
        expect(serverRepositoryMock.save(serverMock)).andReturn(serverMock);
        expect(userServiceMock.findByLogin(USER_NAME)).andReturn(userMock);
        userServiceMock.addServer(userMock, serverMock);
        expectLastCall();
        expect(agentConnectorMock.getHypervisors()).andReturn(new ArrayList<Hypervisor>());

        replay(serverRepositoryMock, serverMock, userServiceMock, userMock, agentConnectorMock);

        ServerNode server = testInstance.createServer(serverMock, USER_NAME);
        verify(serverRepositoryMock, serverMock, userServiceMock, userMock, agentConnectorMock);

        assertEquals(serverMock, server);
    }

    @Test
    public void testGetServerWhenAllowed() throws Exception {
        ServerNode server = testGetServer(Lists.newArrayList(serverMock));
        assertEquals(serverMock, server);
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetServerWhenNotAllowed() throws Exception {
        testGetServer(new ArrayList<ServerNode>());
    }

    private ServerNode testGetServer(List<ServerNode> servers) {
        getServerPrepare(servers);

        replay(userServiceMock, userMock, templateMock, serverRepositoryMock, serverMock);

        ServerNode server = testInstance.getServerNodeIfAllowed(USER_NAME, ID);

        verify(userServiceMock, userMock, templateMock, serverRepositoryMock, serverMock);
        return server;
    }

    @Test
    public void testSetPortAndIPIfAllowed() throws Exception {
        testSetPortAndIP(Lists.newArrayList(serverMock));
    }

    @Test(expected = AccessDeniedException.class)
    public void testSetPortAndIPIfNotAllowed() throws Exception {
        testSetPortAndIP(new ArrayList<ServerNode>());
    }

    private void testSetPortAndIP(List<ServerNode> servers) {
        getServerPrepare(servers);

        ChangeIpAndPortData changeIpAndPortData = createMock(ChangeIpAndPortData.class);
        expect(changeIpAndPortData.getIpAddress()).andReturn(IP);
        serverMock.setIpAddress(IP);
        expectLastCall();
        expect(changeIpAndPortData.getPort()).andReturn(PORT);
        serverMock.setAgentPort(PORT);
        expectLastCall();
        expect(serverRepositoryMock.save(serverMock)).andReturn(serverMock);

        replay(userServiceMock, userMock, templateMock, changeIpAndPortData, serverMock, serverRepositoryMock);

        testInstance.setIPAndPort(changeIpAndPortData, ID, USER_NAME);

        verify(userServiceMock, userMock, templateMock, changeIpAndPortData, serverMock, serverRepositoryMock);
    }

    @Test
    public void testRemoveVMIfAllowed() throws Exception {
        testRemoveServer(Lists.newArrayList(serverMock));
    }

    @Test(expected = AccessDeniedException.class)
    public void testRemoveVMIfNotAllowed() throws Exception {
        testRemoveServer(new ArrayList<ServerNode>());
    }

    private void testRemoveServer(List<ServerNode> servers) {
        getServerPrepare(servers);

        serverRepositoryMock.deleteWithSubtree(serverMock);
        expectLastCall();

        replay(serverMock, userServiceMock, userMock, templateMock, serverRepositoryMock);

        testInstance.removeServer(ID, USER_NAME);

        verify(serverMock, userServiceMock, userMock, templateMock, serverRepositoryMock);
    }

    private void getServerPrepare(List<ServerNode> servers) {
        expect(userServiceMock.findByLogin(USER_NAME)).andReturn(userMock);
        expect(userMock.getServers()).andReturn(servers);
        expect(templateMock.fetch(servers)).andReturn(servers);
        expect(serverRepositoryMock.findOne(ID)).andReturn(serverMock);
    }

    @Test
    public void testAddHypervisor() throws Exception {
        List<Hypervisor> hypervisors = new ArrayList<>();

        expect(serverMock.getHypervisors()).andReturn(hypervisors);
        expect(serverRepositoryMock.save(serverMock)).andReturn(serverMock);

        replay(serverRepositoryMock, serverMock, hypervisorMock);

        testInstance.addHypervisor(serverMock, hypervisorMock);
        verify(serverRepositoryMock, serverMock, hypervisorMock);

        assertEquals(1, hypervisors.size());
        assertEquals(hypervisorMock, hypervisors.get(0));
    }

    private static class ServerServiceMock extends ServerService {

        private AgentConnector agentConnector;

        public ServerServiceMock(AgentConnector agentConnector) {
            this.agentConnector = agentConnector;
        }

        @Override
        protected AgentConnector createAgentConnector(ServerNode serverNode) {
            return agentConnector;
        }
    }
}
