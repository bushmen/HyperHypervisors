package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This class provides functionalities used to authorize users.
 */

@Controller
@RequestMapping(value = "/auth")
public class AuthController {

    /**
     * Called to retrieve name of login view
     *
     * @return String indicating login view
     */

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "auth/login";
    }

    /**
     * Called when wrong user or password is used.
     *
     * @param model instance of Spring MVC Model class
     * @return String indication login view
     */

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "auth/login";
    }
}
