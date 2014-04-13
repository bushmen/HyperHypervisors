package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.UserRepository;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class AdminControllerTest {

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String USER_CREATE_PATH = "admin/users/create";
    public static final String REDIRECT_HOME_PATH = "redirect:/";

    @Test
    public void testCreateView() throws Exception {
        User userMock = createMock(User.class);

        Model modelMock = createMock(Model.class);
        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);

        AdminController testInstance = new AdminController();
        String result = testInstance.create(userMock, modelMock);
        assertEquals(USER_CREATE_PATH, result);
    }

    @Test
    public void testCreateNewUser() throws Exception {
        User userMock = createMock(User.class);
        expect(userMock.getLogin()).andReturn(LOGIN);
        expect(userMock.getPassword()).andReturn(PASSWORD);
        userMock.setPassword(ENCODED_PASSWORD);
        expectLastCall();

        UserRepository userRepositoryMock = createMock(UserRepository.class);
        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(null);
        expect(userRepositoryMock.save(userMock)).andReturn(userMock);

        PasswordEncoder passwordEncoderMock = createMock(PasswordEncoder.class);
        expect(passwordEncoderMock.encode(PASSWORD)).andReturn(ENCODED_PASSWORD);

        BindingResult bindingResultMock = createMock(BindingResult.class);
        expect(bindingResultMock.hasErrors()).andReturn(false);

        Model modelMock = createMock(Model.class);

        replay(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        AdminController testInstance = new AdminController();
        testInstance.userRepository = userRepositoryMock;
        testInstance.passwordEncoder = passwordEncoderMock;

        String result = testInstance.create(userMock, bindingResultMock, modelMock);
        assertEquals(REDIRECT_HOME_PATH, result);
    }

    @Test
    public void testCreateExistingUser() throws Exception {
        User userMock = createMock(User.class);
        expect(userMock.getLogin()).andReturn(LOGIN);

        UserRepository userRepositoryMock = createMock(UserRepository.class);
        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(userMock);

        PasswordEncoder passwordEncoderMock = createMock(PasswordEncoder.class);

        BindingResult bindingResultMock = createMock(BindingResult.class);
        expect(bindingResultMock.hasErrors()).andReturn(false);
        bindingResultMock.addError((FieldError) anyObject());
        expectLastCall();

        Model modelMock = createMock(Model.class);
        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);

        replay(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        AdminController testInstance = new AdminController();
        testInstance.userRepository = userRepositoryMock;
        testInstance.passwordEncoder = passwordEncoderMock;

        String result = testInstance.create(userMock, bindingResultMock, modelMock);
        assertEquals("admin/users/create", result);
    }

    @Test
    public void testCreateUserWithErrors() throws Exception {
        User userMock = createMock(User.class);
        UserRepository userRepositoryMock = createMock(UserRepository.class);
        PasswordEncoder passwordEncoderMock = createMock(PasswordEncoder.class);

        BindingResult bindingResultMock = createMock(BindingResult.class);
        expect(bindingResultMock.hasErrors()).andReturn(true);
        expectLastCall();

        Model modelMock = createMock(Model.class);
        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);

        replay(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        AdminController testInstance = new AdminController();
        testInstance.userRepository = userRepositoryMock;
        testInstance.passwordEncoder = passwordEncoderMock;

        String result = testInstance.create(userMock, bindingResultMock, modelMock);
        assertEquals("admin/users/create", result);
    }

    @Test
    public void testCreateUserSaveException() throws Exception {
        User userMock = createMock(User.class);
        expect(userMock.getLogin()).andReturn(LOGIN);
        expect(userMock.getPassword()).andReturn(PASSWORD);
        userMock.setPassword(ENCODED_PASSWORD);
        expectLastCall();

        UserRepository userRepositoryMock = createMock(UserRepository.class);
        expect(userRepositoryMock.findByLogin(LOGIN)).andReturn(null);
        expect(userRepositoryMock.save(userMock)).andThrow(new RuntimeException());

        PasswordEncoder passwordEncoderMock = createMock(PasswordEncoder.class);
        expect(passwordEncoderMock.encode(PASSWORD)).andReturn(ENCODED_PASSWORD);

        BindingResult bindingResultMock = createMock(BindingResult.class);
        expect(bindingResultMock.hasErrors()).andReturn(false);

        Model modelMock = createMock(Model.class);
        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);

        replay(userMock, userRepositoryMock, passwordEncoderMock, bindingResultMock, modelMock);

        AdminController testInstance = new AdminController();
        testInstance.userRepository = userRepositoryMock;
        testInstance.passwordEncoder = passwordEncoderMock;

        String result = testInstance.create(userMock, bindingResultMock, modelMock);
        assertEquals("admin/users/create", result);
    }
}
