package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.UserRepository;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class AdminControllerTests {

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String USER_CREATE_PATH = "admin/users/create";
    public static final String REDIRECT_HOME_PATH = "redirect:/";

    private User userMock;
    private UserRepository userRepositoryMock;
    private Model modelMock;
    private PasswordEncoder passwordEncoderMock;
    private BindingResult bindingResultMock;
    private AdminController testInstance;

    @Before
    public void setUp() throws Exception {
        userMock = createMock(User.class);
        userRepositoryMock = createMock(UserRepository.class);
        modelMock = createMock(Model.class);
        passwordEncoderMock = createMock(PasswordEncoder.class);
        bindingResultMock = createMock(BindingResult.class);

        testInstance = new AdminController();
        testInstance.userRepository = userRepositoryMock;
        testInstance.passwordEncoder = passwordEncoderMock;
    }

    @Test
    public void testCreateView() throws Exception {
        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);
        replay(userMock, modelMock);

        String result = testInstance.create(userMock, modelMock);
        verify(userMock, modelMock);

        assertEquals(USER_CREATE_PATH, result);
    }

    @Test
    public void testCreateNewUser() throws Exception {
        expect(userMock.getLogin()).andReturn(LOGIN);
        expect(userMock.getPassword()).andReturn(PASSWORD);
        userMock.setPassword(ENCODED_PASSWORD);
        expectLastCall();

        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(null);
        expect(userRepositoryMock.save(userMock)).andReturn(userMock);

        expect(passwordEncoderMock.encode(PASSWORD)).andReturn(ENCODED_PASSWORD);
        expect(bindingResultMock.hasErrors()).andReturn(false);
        replay(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        String result = testInstance.create(userMock, bindingResultMock, modelMock);
        verify(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        assertEquals(REDIRECT_HOME_PATH, result);
    }

    @Test
    public void testCreateExistingUser() throws Exception {
        expect(userMock.getLogin()).andReturn(LOGIN);
        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(userMock);

        expect(bindingResultMock.hasErrors()).andReturn(false);
        bindingResultMock.addError((FieldError) anyObject());
        expectLastCall();

        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);
        replay(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        String result = testInstance.create(userMock, bindingResultMock, modelMock);
        verify(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        assertEquals("admin/users/create", result);
    }

    @Test
    public void testCreateUserWithErrors() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(true);
        expectLastCall();

        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);
        replay(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        String result = testInstance.create(userMock, bindingResultMock, modelMock);
        verify(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        assertEquals("admin/users/create", result);
    }

    @Test
    public void testCreateUserSaveException() throws Exception {
        expect(userMock.getLogin()).andReturn(LOGIN);
        expect(userMock.getPassword()).andReturn(PASSWORD);
        userMock.setPassword(ENCODED_PASSWORD);
        expectLastCall();

        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(null);
        expect(userRepositoryMock.save(userMock)).andThrow(new RuntimeException());

        expect(passwordEncoderMock.encode(PASSWORD)).andReturn(ENCODED_PASSWORD);
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);
        replay(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        String result = testInstance.create(userMock, bindingResultMock, modelMock);
        verify(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        assertEquals("admin/users/create", result);
    }
}
