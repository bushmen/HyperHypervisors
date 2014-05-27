package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangePasswordData;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.UserRepository;

import java.security.Principal;

import static org.easymock.EasyMock.*;

public class MainControllerTests {

    public static final String NEW_PASSWORD = "NEW_PASSWORD";
    private static final String USER_NAME = "USER_NAME";
    private static final String NEW_PASSWORD2 = "DIFFERENT_NEW_PASSWORD";
    private ChangePasswordData changePasswordDataMock;
    private BindingResult bindingResultMock;
    private Principal principalMock;
    private UserRepository userRepositoryMock;
    private PasswordEncoder passwordEncoderMock;
    private User userMock;
    private Model modelMock;
    private MainController testInstance;

    @Before
    public void setUp() throws Exception {
        userRepositoryMock = createMock(UserRepository.class);
        passwordEncoderMock = createMock(PasswordEncoder.class);
        changePasswordDataMock = createMock(ChangePasswordData.class);
        bindingResultMock = createMock(BindingResult.class);
        principalMock = createMock(Principal.class);
        userMock = createMock(User.class);
        modelMock = createMock(Model.class);

        testInstance = new MainController();
        testInstance.userRepository = userRepositoryMock;
        testInstance.passwordEncoder = passwordEncoderMock;
    }

    @Test
    public void testIndex() throws Exception {
        Assert.assertEquals("index", testInstance.index());
    }

    @Test
    public void testChangePasswordView() throws Exception {
        replay(changePasswordDataMock);
        String result = testInstance.changePasswordView(changePasswordDataMock);
        verify(changePasswordDataMock);
        Assert.assertEquals("changePassword", result);
    }

    @Test
    public void testChangePasswordSuccess() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(changePasswordDataMock.getNewPassword()).andReturn(NEW_PASSWORD).times(2);
        expect(changePasswordDataMock.getNewPasswordRepeated()).andReturn(NEW_PASSWORD);
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(userRepositoryMock.findByLogin(USER_NAME)).andReturn(userMock);
        expect(passwordEncoderMock.encode(NEW_PASSWORD)).andReturn(NEW_PASSWORD);
        userMock.setPassword(NEW_PASSWORD);
        expectLastCall();
        expect(userRepositoryMock.save(userMock)).andReturn(userMock);

        replay(bindingResultMock, changePasswordDataMock, passwordEncoderMock,
                principalMock, userRepositoryMock, userMock);

        String result = testInstance.changePassword(changePasswordDataMock, bindingResultMock, principalMock);

        verify(bindingResultMock, changePasswordDataMock, passwordEncoderMock,
                principalMock, userRepositoryMock, userMock);
        Assert.assertEquals("redirect:/", result);
    }

    @Test
    public void testChangePasswordFormErrors() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(true);

        replay(bindingResultMock, changePasswordDataMock, passwordEncoderMock,
                principalMock, userRepositoryMock, userMock);

        String result = testInstance.changePassword(changePasswordDataMock, bindingResultMock, principalMock);

        verify(bindingResultMock, changePasswordDataMock, passwordEncoderMock,
                principalMock, userRepositoryMock, userMock);
        Assert.assertEquals("changePassword", result);
    }

    @Test
    public void testChangePasswordDifferentPasswords() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(changePasswordDataMock.getNewPassword()).andReturn(NEW_PASSWORD);
        expect(changePasswordDataMock.getNewPasswordRepeated()).andReturn(NEW_PASSWORD2);
        bindingResultMock.addError(EasyMock.<ObjectError>anyObject());
        expectLastCall();

        replay(bindingResultMock, changePasswordDataMock, passwordEncoderMock,
                principalMock, userRepositoryMock, userMock);

        String result = testInstance.changePassword(changePasswordDataMock, bindingResultMock, principalMock);

        verify(bindingResultMock, changePasswordDataMock, passwordEncoderMock,
                principalMock, userRepositoryMock, userMock);
        Assert.assertEquals("changePassword", result);
    }

    @Test
    public void testAccessDenied() throws Exception {
        expect(modelMock.addAttribute("accessDenied", true)).andReturn(modelMock);

        replay(userRepositoryMock, passwordEncoderMock, modelMock);

        String result = testInstance.accessDenied(modelMock);

        verify(userRepositoryMock, passwordEncoderMock, modelMock);
        Assert.assertEquals("index", result);
    }
}
