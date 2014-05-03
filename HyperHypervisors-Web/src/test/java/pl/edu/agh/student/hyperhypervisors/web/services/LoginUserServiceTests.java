package pl.edu.agh.student.hyperhypervisors.web.services;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.UserRepository;
import pl.edu.agh.student.hyperhypervisors.web.session.UserSessionDetails;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LoginUserServiceTests {

    private static final String LOGIN = "testLogin";
    private User userMock;
    private UserRepository userRepositoryMock;
    private LoginUserService testInstance;

    @Before
    public void setUp() throws Exception {
        userMock = createMock(User.class);
        userRepositoryMock = createMock(UserRepository.class);

        testInstance = new LoginUserService();
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
}
