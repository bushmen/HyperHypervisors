package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class UserTest {
    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setLogin("janek");
    }

    @Test
    public void testGetLogin() throws Exception {
        assertEquals("janek", user.getLogin());
    }
}
