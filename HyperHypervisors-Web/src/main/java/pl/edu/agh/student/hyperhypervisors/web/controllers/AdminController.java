package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.UserRole;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.UserRepository;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/users/create", method = RequestMethod.GET)
    public String create(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("userRoles", UserRole.values());
        return "/admin/users/create";
    }

    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("userRoles", UserRole.values());
            return "admin/users/create";
        }

        if(userRepository.findByLogin(user.getLogin()) != null) {
            result.addError(new FieldError("user", "login", "Login must be unique"));
            model.addAttribute("userRoles", UserRole.values());
            return "admin/users/create";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("userRoles", UserRole.values());
            return "admin/users/create";
        }
    }


}
