package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.junit.Test;
import org.springframework.ui.Model;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class AuthControllerTest {

    public static final String AUTH_LOGIN_VIEW_PATH = "auth/login";

    @Test
    public void testLoginView() throws Exception {
        AuthController testInstance = new AuthController();
        String result = testInstance.login();
        assertEquals(AUTH_LOGIN_VIEW_PATH, result);
    }

    @Test
    public void testLoginErrorView() throws Exception {
        Model modelMock = createMock(Model.class);
        expect(modelMock.addAttribute(anyString(), anyBoolean())).andReturn(modelMock);
        replay(modelMock);

        AuthController testInstance = new AuthController();
        String result = testInstance.loginError(modelMock);
        assertEquals(AUTH_LOGIN_VIEW_PATH, result);
    }
}
