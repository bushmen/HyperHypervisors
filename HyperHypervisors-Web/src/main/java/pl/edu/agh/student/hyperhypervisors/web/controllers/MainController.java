package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangePasswordData;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.UserRepository;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class MainController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePasswordView(@ModelAttribute(value = "passwordData") ChangePasswordData passwordData) {
        return "changePassword";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePasswordView(@Valid @ModelAttribute(value = "passwordData") ChangePasswordData passwordData,
                                     BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "changePassword";
        }

        if (!passwordData.getNewPassword().equals(passwordData.getNewPasswordRepeated())) {
            result.addError(new FieldError("passwordData", "newPasswordRepeated", "Passwords are different"));
            return "changePassword";
        }

        User user = userRepository.findByLogin(principal.getName());
        user.setPassword(passwordEncoder.encode(passwordData.getNewPassword()));
        userRepository.save(user);
        return "redirect:/";
    }
}
