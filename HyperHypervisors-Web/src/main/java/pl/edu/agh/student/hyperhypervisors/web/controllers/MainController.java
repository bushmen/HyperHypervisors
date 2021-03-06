package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

/**
 * This controller class is used for general purpose functionalities.
 */

@Controller
public class MainController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * @return name of main page view
     */

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    /**
     * @param passwordData represents information about new user's password
     * @return name of view containing form for changing password
     */

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePasswordView(@ModelAttribute(value = "passwordData") ChangePasswordData passwordData) {
        return "changePassword";
    }

    /**
     * This method is used to change user's password
     *
     * @param passwordData represents information about new user's password
     * @param result       contains information about errors
     * @param principal    instance of Principal class
     * @return String indicating next view or navigation path
     */

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(@Valid @ModelAttribute(value = "passwordData") ChangePasswordData passwordData,
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

    /**
     * This method is called when user tries to access data which he is not allowed to.
     *
     * @param model instance of Spring MVC Model
     * @return name of main page view
     */

    @RequestMapping(value = "/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("accessDenied", true);
        return "index";
    }
}
