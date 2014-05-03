package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.agh.student.hyperhypervisors.model.ServerDescription;
import pl.edu.agh.student.hyperhypervisors.model.VirtualMachineDescription;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;

import java.util.List;

@Controller
@RequestMapping(value = "/auth")
public class AuthController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "auth/login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "auth/login";
    }

    @RequestMapping(value = "/loginVM", method = RequestMethod.GET)
    public
    @ResponseBody
    VirtualMachineDescription loginVM(@RequestParam String machineName) {
        try {
            ServerNode localMachine = new ServerNode();
            localMachine.setIpAddress("127.0.0.1");
            localMachine.setAgentPort(9999);
            return new AgentConnector(localMachine).getVirtualMachineDescription(machineName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/loginVMs", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> loginVM() {
        try {
            ServerNode localMachine = new ServerNode();
            localMachine.setIpAddress("127.0.0.1");
            localMachine.setAgentPort(9999);
            return new AgentConnector(localMachine).getVirtualMachinesNames();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/loginServer", method = RequestMethod.GET)
    public
    @ResponseBody
    ServerDescription loginServer() {
        try {
            ServerNode localMachine = new ServerNode();
            localMachine.setIpAddress("127.0.0.1");
            localMachine.setAgentPort(9999);
            return new AgentConnector(localMachine).getServerDescription();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
