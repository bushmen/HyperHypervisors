package pl.edu.agh.student.hyperhypervisors.web.services;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.UserRepository;
import pl.edu.agh.student.hyperhypervisors.web.session.UserSessionDetails;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class UserServiceTests {

    private static final String LOGIN = "testLogin";
    private User userMock;
    private ServerNode serverMock;
    private UserRepository userRepositoryMock;
    private UserService testInstance;

    @Before
    public void setUp() throws Exception {
        userMock = createMock(User.class);
        serverMock = createMock(ServerNode.class);
        userRepositoryMock = createMock(UserRepository.class);

        testInstance = new UserService();
        testInstance.userRepository = userRepositoryMock;
    }

    @Test
    public void testLoadExistingUserByName() {
        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(userMock);
        replay(userMock, userRepositoryMock);

        UserSessionDetails result = testInstance.loadUserByUsername(LOGIN);
        verify(userMock, userRepositoryMock);

        assertNotNull(result);
        assertEquals(userMock, result.getUser());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadNonExistingUserByName() {
        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(null);
        replay(userRepositoryMock);

        testInstance.loadUserByUsername(LOGIN);
        verify(userRepositoryMock);
    }

    @Test
    public void testFindByLogin() throws Exception {
        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(userMock);
        replay(userRepositoryMock);

        testInstance.findByLogin(LOGIN);
        verify(userRepositoryMock);
    }

    @Test
    public void testAddServer() throws Exception {
        List<ServerNode> servers = new ArrayList<>();
        expect(userMock.getServers()).andReturn(servers);
        expect(userRepositoryMock.save(userMock)).andReturn(userMock);
        replay(userMock, userRepositoryMock);

        testInstance.addServer(userMock, serverMock);
        verify(userMock, userRepositoryMock);
        assertTrue(servers.contains(serverMock));
    }
}
