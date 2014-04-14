package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class AuthControllerTests {

    public static final String AUTH_LOGIN_VIEW_PATH = "auth/login";

    private Model modelMock;
    private AuthController testInstance;

    @Before
    public void setUp() throws Exception {
        modelMock = createMock(Model.class);

        testInstance = new AuthController();
    }

    @Test
    public void testLoginView() throws Exception {
        String result = testInstance.login();
        assertEquals(AUTH_LOGIN_VIEW_PATH, result);
    }

    @Test
    public void testLoginErrorView() throws Exception {
        expect(modelMock.addAttribute(anyString(), anyBoolean())).andReturn(modelMock);
        replay(modelMock);

        String result = testInstance.loginError(modelMock);
        verify(modelMock);

        assertEquals(AUTH_LOGIN_VIEW_PATH, result);
    }
}
