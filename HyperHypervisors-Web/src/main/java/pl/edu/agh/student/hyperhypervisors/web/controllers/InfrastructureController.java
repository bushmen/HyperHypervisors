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

/**
 * This class provides an API to retrieve information about stored infrastructure and allows to add new servers and update underlying data.
 */

@Controller
@RequestMapping(value = "/infrastructure")
public class InfrastructureController {

    @Autowired
    ServerService serverService;

    @Autowired
    HypervisorService hypervisorService;

    @Autowired
    VirtualMachineService virtualMachineService;

    /**
     * @return String containing name of the main infrastructure view
     */

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String infrastructureView() {
        return "infrastructure/view";
    }

    /**
     * @param principal instance of Principal class
     * @return List containing information about known servers
     * @throws Exception
     */

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ServerData> getServersData(Principal principal) throws Exception {
        String userName = principal.getName();
        return serverService.getServersData(userName);
    }

    /**
     * @param server represents added server
     * @return name of view with form for creating server
     */

    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public String createServerView(@ModelAttribute(value = "server") ServerNode server) {
        return "infrastructure/create-server";
    }

    /**
     * Method used to add new server
     *
     * @param server    represents added server
     * @param result    contains information about errors
     * @param principal instance of Principal class
     * @return String indicating next view or navigation path
     * @throws Exception
     */

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

    /**
     * @param server    represents new IP and port to be set for server
     * @param serverId  ID from database for which update is going to be performed
     * @param principal instance of Principal class
     * @return name of view containing form to update IP and port
     */

    @RequestMapping(value = "/server/{serverId}/new-ip-and-port", method = RequestMethod.GET)
    public String setServerIPAndPortView(@ModelAttribute(value = "server") ChangeIpAndPortData server,
                                         @ModelAttribute @PathVariable Long serverId, Principal principal) {
        serverService.getServerNodeIfAllowed(principal.getName(), serverId);
        return "infrastructure/set-ip-and-port";
    }

    /**
     * This method is used to update server's IP address and port on which the agent is accessible.
     *
     * @param server    represents new IP and port to be set for server
     * @param serverId  ID from database for which update is going to be performed
     * @param principal instance of Principal class
     * @return String indicating next view or navigation path
     */

    @RequestMapping(value = "/server/{serverId}/new-ip-and-port", method = RequestMethod.POST)
    public String setServerIPAndPort(@Valid @ModelAttribute(value = "server") ChangeIpAndPortData server, BindingResult result,
                                     @ModelAttribute @PathVariable Long serverId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-ip-and-port";
        }

        serverService.setIPAndPort(server, serverId, principal.getName());
        return "redirect:/infrastructure";
    }

    /**
     * This method is used to remove server with all underlying data - hypervisors, virtual machines, etc.
     *
     * @param serverId  ID from database for which deletion is going to be performed
     * @param principal instance of Principal class
     * @return String indicating navigation to main infrastructure view
     */

    @RequestMapping(value = "/server/{serverId}", method = RequestMethod.GET)
    public String removeServer(@PathVariable Long serverId, Principal principal) {
        serverService.removeServer(serverId, principal.getName());
        return "redirect:/infrastructure";
    }

    /**
     * @param hypervisor   represents credentials to be set for hypervisor
     * @param hypervisorId ID from database for which update is going to be performed
     * @param principal    instance of Principal class
     * @return name of view with form used to change credentials for hypervisor
     */

    @RequestMapping(value = "/hypervisor/{hypervisorId}/change-credentials", method = RequestMethod.GET)
    public String setHypervisorLoginAndPasswordView(@ModelAttribute(value = "hypervisor") ChangeLoginAndPassword hypervisor,
                                                    @ModelAttribute @PathVariable Long hypervisorId, Principal principal) {
        hypervisorService.getHypervisorIfAllowed(principal.getName(), hypervisorId);
        return "infrastructure/set-login-and-password";
    }

    /**
     * This method is used to change credentials for chosen hypervisor.
     *
     * @param hypervisor   represents credentials to be set for hypervisor
     * @param result       contains information about errors
     * @param hypervisorId ID from database for which update is going to be performed
     * @param principal    instance of Principal class
     * @return String indicating next view or navigation path
     */

    @RequestMapping(value = "/hypervisor/{hypervisorId}/change-credentials", method = RequestMethod.POST)
    public String setHypervisorLoginAndPassword(@Valid @ModelAttribute(value = "hypervisor") ChangeLoginAndPassword hypervisor,
                                                BindingResult result, @ModelAttribute @PathVariable Long hypervisorId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-login-and-password";
        }

        hypervisorService.setLoginAndPassword(hypervisor, hypervisorId, principal.getName());
        return "redirect:/infrastructure";
    }

    /**
     * @param vm        represents new IP and port to be set for virtual machine
     * @param vmId      ID from database for which update is going to be performed
     * @param principal instance of Principal class
     * @return name of view with form used to change IP and port for virtual machine
     */

    @RequestMapping(value = "/vm/{vmId}/new-ip-and-port", method = RequestMethod.GET)
    public String setVMIPAddressAndPortView(@ModelAttribute(value = "vm") ChangeIpAndPortData vm,
                                            @ModelAttribute @PathVariable Long vmId, Principal principal) {
        virtualMachineService.getVirtualMachineIfAllowed(principal.getName(), vmId);
        return "infrastructure/set-vm-ip-and-port";
    }

    /**
     * This method is used to change IP address and port on which agent for virtual machine is accessible
     *
     * @param vm        represents new IP and port to be set for virtual machine
     * @param result    contains information about errors
     * @param vmId      ID from database for which update is going to be performed
     * @param principal instance of Principal class
     * @return String indicating next view or navigation path
     */

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
