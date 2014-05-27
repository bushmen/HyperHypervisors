package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAddressData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAndPortData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangePortData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ServerData;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ApplicationServer;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.services.ApplicationServerService;
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

    @Autowired
    ApplicationServerService applicationServerService;

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
                               BindingResult result, Principal principal) {
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

        serverService.setIPAndPort(server, serverId, principal.getName());
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/server/{serverId}", method = RequestMethod.GET)
    public String removeServer(@PathVariable Long serverId, Principal principal) {
        serverService.removeServer(serverId, principal.getName());
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/server/{serverId}/new-child", method = RequestMethod.GET)
    public String createHypervisorView(@ModelAttribute(value = "hypervisor") Hypervisor hypervisor,
                                       @ModelAttribute @PathVariable Long serverId, Principal principal) {
        serverService.getServerNodeIfAllowed(principal.getName(), serverId);
        return "infrastructure/create-hypervisor";
    }

    @RequestMapping(value = "/server/{serverId}/new-child", method = RequestMethod.POST)
    public String createHypervisor(@Valid @ModelAttribute(value = "hypervisor") Hypervisor hypervisor, BindingResult result,
                                   @ModelAttribute @PathVariable Long serverId, Principal principal) throws Exception {
        if (result.hasErrors()) {
            return "infrastructure/create-hypervisor";
        }

        ServerNode serverNode = serverService.getServerNodeIfAllowed(principal.getName(), serverId);
        hypervisorService.createHypervisor(hypervisor, serverNode);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/hypervisor/{hypervisorId}/new-port", method = RequestMethod.GET)
    public String setHypervisorPortView(@ModelAttribute(value = "hypervisor") ChangePortData hypervisor,
                                        @ModelAttribute @PathVariable Long hypervisorId, Principal principal) {
        hypervisorService.getHypervisorIfAllowed(principal.getName(), hypervisorId);
        return "infrastructure/set-port-hypervisor";
    }

    @RequestMapping(value = "/hypervisor/{hypervisorId}/new-port", method = RequestMethod.POST)
    public String setHypervisorPort(@Valid @ModelAttribute(value = "hypervisor") ChangePortData hypervisor, BindingResult result,
                                    @ModelAttribute @PathVariable Long hypervisorId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-port-hypervisor";
        }

        hypervisorService.setPort(hypervisor, hypervisorId, principal.getName());
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/hypervisor/{hypervisorId}", method = RequestMethod.GET)
    public String removeHypervisor(@PathVariable Long hypervisorId, Principal principal) {
        String userName = principal.getName();
        hypervisorService.removeHypervisor(hypervisorId, userName);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/vm/{vmId}/new-ip", method = RequestMethod.GET)
    public String setVMIPAddressView(@ModelAttribute(value = "vm") ChangeIpAddressData vm,
                                     @ModelAttribute @PathVariable Long vmId, Principal principal) {
        virtualMachineService.getVirtualMachineIfAllowed(principal.getName(), vmId);
        return "infrastructure/set-ip";
    }

    @RequestMapping(value = "/vm/{vmId}/new-ip", method = RequestMethod.POST)
    public String setVMIPAddress(@Valid @ModelAttribute(value = "vm") ChangeIpAddressData vm, BindingResult result,
                                 @ModelAttribute @PathVariable Long vmId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-ip";
        }

        virtualMachineService.setIPAddress(vm, vmId, principal.getName());
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/vm/{vmId}/new-child", method = RequestMethod.GET)
    public String createApplicationServerView(@ModelAttribute(value = "appServer") ApplicationServer appServer,
                                              @ModelAttribute @PathVariable Long vmId, Principal principal, Model model) {
        virtualMachineService.getVirtualMachineIfAllowed(principal.getName(), vmId);
        model.addAttribute("appServerTypes", ApplicationServer.Type.values());
        return "infrastructure/create-appServer";
    }

    @RequestMapping(value = "/vm/{vmId}/new-child", method = RequestMethod.POST)
    public String createApplicationServer(@Valid @ModelAttribute(value = "appServer") ApplicationServer appServer, BindingResult result,
                                          @ModelAttribute @PathVariable Long vmId, Principal principal, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("appServerTypes", ApplicationServer.Type.values());
            return "infrastructure/create-appServer";
        }

        String userName = principal.getName();
        applicationServerService.createApplicationServer(appServer, vmId, userName);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/appServer/{appServerId}/new-port", method = RequestMethod.GET)
    public String setAppServerPortView(@ModelAttribute(value = "appServer") ChangePortData appServer,
                                       @ModelAttribute @PathVariable Long appServerId, Principal principal) {
        applicationServerService.getApplicationServerIfAllowed(principal.getName(), appServerId);
        return "infrastructure/set-port-appserver";
    }

    @RequestMapping(value = "/appServer/{appServerId}/new-port", method = RequestMethod.POST)
    public String setAppServerPort(@Valid @ModelAttribute(value = "appServer") ChangePortData appServer, BindingResult result,
                                   @ModelAttribute @PathVariable Long appServerId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-port-appserver";
        }

        applicationServerService.setJmxPort(appServer, appServerId, principal.getName());
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/appServer/{appServerId}", method = RequestMethod.GET)
    public String removeApplicationServer(@PathVariable Long appServerId, Principal principal) {
        applicationServerService.removeApplicationServer(appServerId, principal.getName());
        return "redirect:/infrastructure";
    }
}
