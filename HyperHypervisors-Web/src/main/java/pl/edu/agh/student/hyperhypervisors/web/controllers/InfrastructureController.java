package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAddressData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ServerData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.*;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.*;
import pl.edu.agh.student.hyperhypervisors.web.services.InfrastructureService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping(value = "/infrastructure")
public class InfrastructureController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    InfrastructureService infrastructureService;

    @Autowired
    ServerNodeRepository serverNodeRepository;

    @Autowired
    HypervisorRepository hypervisorRepository;

    @Autowired
    VirtualMachineRepository virtualMachineRepository;

    @Autowired
    ApplicationServerRepository applicationServerRepository;

    @Autowired
    Neo4jOperations template;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String infrastructureView() {
        return "infrastructure/view";
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ServerData> getServersData(Principal principal) throws Exception {
        User user = userRepository.findByLogin(principal.getName());
        return infrastructureService.getServersData(user);
    }

    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public String createServerView(@ModelAttribute(value = "server") ServerNode serverNode) {
        return "infrastructure/create-server";
    }

    @RequestMapping(value = "/server", method = RequestMethod.POST)
    public String createServer(@Valid @ModelAttribute(value = "server") ServerNode serverNode,
                               BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/create-server";
        }

        ServerNode savedServerNode = serverNodeRepository.save(serverNode);
        User user = userRepository.findByLogin(principal.getName());
        user.getServers().add(savedServerNode);
        userRepository.save(user);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/server/{serverId}/new-child", method = RequestMethod.GET)
    public String createHypervisorView(@ModelAttribute(value = "hypervisor") Hypervisor hypervisor,
                                       @ModelAttribute @PathVariable Long serverId) {
        return "infrastructure/create-hypervisor";
    }

    @RequestMapping(value = "/server/{serverId}/new-child", method = RequestMethod.POST)
    public String createHypervisor(@Valid @ModelAttribute(value = "hypervisor") Hypervisor hypervisor, BindingResult result,
                                   @ModelAttribute @PathVariable Long serverId, Principal principal) throws Exception {
        if (result.hasErrors()) {
            return "infrastructure/create-hypervisor";
        }

        ServerNode serverNode = serverNodeRepository.findOne(serverId);
        User user = userRepository.findByLogin(principal.getName());
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());
        if (!userServerNodes.contains(serverNode)) {
            //TODO not allowed
            return "redirect:/";
        }

        Hypervisor savedHypervisor = hypervisorRepository.save(hypervisor);
        List<String> vmNames = new AgentConnector(serverNode).getVirtualMachinesNames(hypervisor);
        for (String vmName : vmNames) {
            VirtualMachine vm = new VirtualMachine();
            vm.setName(vmName);
            VirtualMachine savedVM = virtualMachineRepository.save(vm);
            savedHypervisor.getVirtualMachines().add(savedVM);
        }
        savedHypervisor = hypervisorRepository.save(savedHypervisor);
        serverNode.getHypervisors().add(savedHypervisor);
        serverNodeRepository.save(serverNode);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/vm/{vmId}/new-ip", method = RequestMethod.GET)
    public String setVMIPAddressView(@ModelAttribute(value = "vm") ChangeIpAddressData vm, @ModelAttribute @PathVariable Long vmId) {
        return "infrastructure/set-vm-ip";
    }

    @RequestMapping(value = "/vm/{vmId}/new-ip", method = RequestMethod.POST)
    public String setVMIPAddress(@Valid @ModelAttribute(value = "vm") ChangeIpAddressData vm, BindingResult result,
                                 @ModelAttribute @PathVariable Long vmId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-vm-ip";
        }

        User user = userRepository.findByLogin(principal.getName());
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());
        VirtualMachine virtualMachine = virtualMachineRepository.findOne(vmId);
        virtualMachine.setIpAddress(vm.getIpAddress());

        //TODO can do better
        Hypervisor vmsHypervisor = null;
        for (ServerNode serverNode : userServerNodes) {
            Collection<Hypervisor> serversHypervisors = template.fetch(serverNode.getHypervisors());
            for (Hypervisor hypervisor : serversHypervisors) {
                Collection<VirtualMachine> virtualMachines = template.fetch(hypervisor.getVirtualMachines());
                if (virtualMachines.contains(virtualMachine)) {
                    vmsHypervisor = hypervisor;
                    break;
                }
            }
        }
        if (vmsHypervisor == null) {
            //TODO not allowed
            return "redirect:/";
        }

        VirtualMachine savedVirtualMachine = virtualMachineRepository.save(virtualMachine);
        vmsHypervisor.getVirtualMachines().add(savedVirtualMachine);
        hypervisorRepository.save(vmsHypervisor);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/vm/{vmId}/new-child", method = RequestMethod.GET)
    public String createApplicationServerView(@ModelAttribute(value = "appServer") ApplicationServer appServer,
                                              @ModelAttribute @PathVariable Long vmId) {
        return "infrastructure/create-appServer";
    }

    @RequestMapping(value = "/vm/{vmId}/new-child", method = RequestMethod.POST)
    public String createApplicationServer(@Valid @ModelAttribute(value = "appServer") ApplicationServer appServer,
                                          BindingResult result, @ModelAttribute @PathVariable Long vmId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/create-appServer";
        }

        VirtualMachine virtualMachine = virtualMachineRepository.findOne(vmId);
        User user = userRepository.findByLogin(principal.getName());
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());

        //TODO can do better
        boolean allowed = false;
        for (ServerNode serverNode : userServerNodes) {
            Collection<Hypervisor> serversHypervisors = template.fetch(serverNode.getHypervisors());
            for (Hypervisor hypervisor : serversHypervisors) {
                Collection<VirtualMachine> hypervisorsVMs = template.fetch(hypervisor.getVirtualMachines());
                if (hypervisorsVMs.contains(virtualMachine)) {
                    allowed = true;
                    break;
                }
            }
        }
        if (!allowed) {
            //TODO not allowed
            return "redirect:/";
        }

        ApplicationServer savedAppServer = applicationServerRepository.save(appServer);
        virtualMachine.getApplicationServers().add(savedAppServer);
        virtualMachineRepository.save(virtualMachine);
        return "redirect:/infrastructure";
    }
}
