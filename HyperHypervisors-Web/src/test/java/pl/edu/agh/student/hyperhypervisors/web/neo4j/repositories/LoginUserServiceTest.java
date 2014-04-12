package pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories;

import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.session.UserSessionDetails;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LoginUserServiceTest {

    private static final String LOGIN = "testLogin";

    @Test
    public void testLoadExistingUserByName() {
        User userMock = createMock(User.class);

        UserRepository userRepositoryMock = createMock(UserRepository.class);
        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(userMock);

        replay(userMock, userRepositoryMock);

        LoginUserService testInstance = new LoginUserService();
        testInstance.userRepository = userRepositoryMock;

        UserSessionDetails result = testInstance.loadUserByUsername(LOGIN);
        assertNotNull(result);
        assertEquals(userMock, result.getUser());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadNonExistingUserByName() {
        UserRepository userRepositoryMock = createMock(UserRepository.class);
        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(null);
        replay(userRepositoryMock);

        LoginUserService testInstance = new LoginUserService();
        testInstance.userRepository = userRepositoryMock;

        testInstance.loadUserByUsername(LOGIN);
    }
}
