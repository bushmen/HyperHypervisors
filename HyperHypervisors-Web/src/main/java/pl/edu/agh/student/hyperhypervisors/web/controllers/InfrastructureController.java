package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAndPortData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeLoginAndPassword;
import pl.edu.agh.student.hyperhypervisors.web.dto.ServerData;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.services.HypervisorService;
import pl.edu.agh.student.hyperhypervisors.web.services.ServerService;
import pl.edu.agh.student.hyperhypervisors.web.services.VirtualMachineService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(value = "/infrastructure")
public class InfrastructureController {

    @Autowired
    ServerService serverService;

    @Autowired
    HypervisorService hypervisorService;

    @Autowired
    VirtualMachineService virtualMachineService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String infrastructureView() {
        return "infrastructure/view";
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ServerData> getServersData(Principal principal) throws Exception {
        String userName = principal.getName();
        return serverService.getServersData(userName);
    }

    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public String createServerView(@ModelAttribute(value = "server") ServerNode server) {
        return "infrastructure/create-server";
    }

    @RequestMapping(value = "/server", method = RequestMethod.POST)
    public String createServer(@Valid @ModelAttribute(value = "server") ServerNode server,
                               BindingResult result, Principal principal) throws Exception {
        if (result.hasErrors()) {
            return "infrastructure/create-server";
        }

        String userName = principal.getName();
        serverService.createServer(server, userName);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/server/{serverId}/new-ip-and-port", method = RequestMethod.GET)
    public String setServerIPAndPortView(@ModelAttribute(value = "server") ChangeIpAndPortData server,
                                         @ModelAttribute @PathVariable Long serverId, Principal principal) {
        serverService.getServerNodeIfAllowed(principal.getName(), serverId);
        return "infrastructure/set-ip-and-port";
    }

    @RequestMapping(value = "/server/{serverId}/new-ip-and-port", method = RequestMethod.POST)
    public String setServerIPAndPort(@Valid @ModelAttribute(value = "server") ChangeIpAndPortData server, BindingResult result,
                                     @ModelAttribute @PathVariable Long serverId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-ip-and-port";
        }

        serverService.serIPAndPort(server, serverId, principal.getName());
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/server/{serverId}", method = RequestMethod.GET)
    public String removeServer(@PathVariable Long serverId, Principal principal) {
        serverService.removeServer(serverId, principal.getName());
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/hypervisor/{hypervisorId}/change-credentials", method = RequestMethod.GET)
    public String setHypervisorLoginAndPasswordView(@ModelAttribute(value = "hypervisor") ChangeLoginAndPassword hypervisor,
                                                    @ModelAttribute @PathVariable Long hypervisorId, Principal principal) {
        hypervisorService.getHypervisorIfAllowed(principal.getName(), hypervisorId);
        return "infrastructure/set-login-and-password";
    }

    @RequestMapping(value = "/hypervisor/{hypervisorId}/change-credentials", method = RequestMethod.POST)
    public String setHypervisorLoginAndPassword(@Valid @ModelAttribute(value = "hypervisor") ChangeLoginAndPassword hypervisor,
                                                BindingResult result, @ModelAttribute @PathVariable Long hypervisorId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-login-and-password";
        }

        hypervisorService.setLoginAndPassword(hypervisor, hypervisorId, principal.getName());
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/vm/{vmId}/new-ip-and-port", method = RequestMethod.GET)
    public String setVMIPAddressAndPortView(@ModelAttribute(value = "vm") ChangeIpAndPortData vm,
                                     @ModelAttribute @PathVariable Long vmId, Principal principal) {
        virtualMachineService.getVirtualMachineIfAllowed(principal.getName(), vmId);
        return "infrastructure/set-vm-ip-and-port";
    }

    @RequestMapping(value = "/vm/{vmId}/new-ip-and-port", method = RequestMethod.POST)
    public String setVMIPAddressAndPort(@Valid @ModelAttribute(value = "vm") ChangeIpAndPortData vm, BindingResult result,
                                 @ModelAttribute @PathVariable Long vmId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-vm-ip-and-port";
        }

        virtualMachineService.setIPAddressAndPort(vm, vmId, principal.getName());
        return "redirect:/infrastructure";
    }
}
