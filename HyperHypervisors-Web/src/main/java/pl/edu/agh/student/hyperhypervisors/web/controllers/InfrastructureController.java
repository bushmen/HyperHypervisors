package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAddressData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAndPortData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangePortData;
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
    public String createServerView(@ModelAttribute(value = "server") ServerNode server) {
        return "infrastructure/create-server";
    }

    @RequestMapping(value = "/server", method = RequestMethod.POST)
    public String createServer(@Valid @ModelAttribute(value = "server") ServerNode server,
                               BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/create-server";
        }

        ServerNode savedServerNode = serverNodeRepository.save(server);
        User user = userRepository.findByLogin(principal.getName());
        user.getServers().add(savedServerNode);
        userRepository.save(user);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/server/{serverId}/new-ip-and-port", method = RequestMethod.GET)
    public String setServerIPAndPortView(@ModelAttribute(value = "server") ChangeIpAndPortData server,
                                         @ModelAttribute @PathVariable Long serverId, Principal principal) {
        getServerNodeIfAllowed(principal, serverId);
        return "infrastructure/set-ip-and-port";
    }

    @RequestMapping(value = "/server/{serverId}/new-ip-and-port", method = RequestMethod.POST)
    public String setServerIPAndPort(@Valid @ModelAttribute(value = "server") ChangeIpAndPortData server, BindingResult result,
                                     @ModelAttribute @PathVariable Long serverId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-ip-and-port";
        }

        ServerNode serverNode = getServerNodeIfAllowed(principal, serverId);
        serverNode.setIpAddress(server.getIpAddress());
        serverNode.setAgentPort(server.getPort());
        serverNodeRepository.save(serverNode);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/server/{serverId}", method = RequestMethod.GET)
    public String removeServer(@PathVariable Long serverId, Principal principal) {
        ServerNode serverNode = getServerNodeIfAllowed(principal, serverId);
        serverNodeRepository.deleteWithSubtree(serverNode);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/server/{serverId}/new-child", method = RequestMethod.GET)
    public String createHypervisorView(@ModelAttribute(value = "hypervisor") Hypervisor hypervisor,
                                       @ModelAttribute @PathVariable Long serverId, Principal principal) {
        getServerNodeIfAllowed(principal, serverId);
        return "infrastructure/create-hypervisor";
    }

    @RequestMapping(value = "/server/{serverId}/new-child", method = RequestMethod.POST)
    public String createHypervisor(@Valid @ModelAttribute(value = "hypervisor") Hypervisor hypervisor, BindingResult result,
                                   @ModelAttribute @PathVariable Long serverId, Principal principal) throws Exception {
        if (result.hasErrors()) {
            return "infrastructure/create-hypervisor";
        }

        ServerNode serverNode = getServerNodeIfAllowed(principal, serverId);
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

    @RequestMapping(value = "/hypervisor/{hypervisorId}/new-port", method = RequestMethod.GET)
    public String setHypervisorPortView(@ModelAttribute(value = "hypervisor") ChangePortData hypervisor,
                                        @ModelAttribute @PathVariable Long hypervisorId, Principal principal) {
        getHypervisorIfAllowed(principal, hypervisorId);
        return "infrastructure/set-port-hypervisor";
    }

    @RequestMapping(value = "/hypervisor/{hypervisorId}/new-port", method = RequestMethod.POST)
    public String setHypervisorPort(@Valid @ModelAttribute(value = "hypervisor") ChangePortData hypervisor, BindingResult result,
                                    @ModelAttribute @PathVariable Long hypervisorId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-port-hypervisor";
        }

        Hypervisor hypervisorNode = getHypervisorIfAllowed(principal, hypervisorId);
        hypervisorNode.setPort(hypervisor.getPort());
        hypervisorRepository.save(hypervisorNode);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/hypervisor/{hypervisorId}", method = RequestMethod.GET)
    public String removeHypervisor(@PathVariable Long hypervisorId, Principal principal) {
        Hypervisor hypervisorNode = getHypervisorIfAllowed(principal, hypervisorId);
        hypervisorRepository.deleteWithSubtree(hypervisorNode);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/vm/{vmId}/new-ip", method = RequestMethod.GET)
    public String setVMIPAddressView(@ModelAttribute(value = "vm") ChangeIpAddressData vm,
                                     @ModelAttribute @PathVariable Long vmId, Principal principal) {
        VirtualMachine virtualMachine = virtualMachineRepository.findOne(vmId);
        getVirtualMachineHypervisorIfAllowed(principal, virtualMachine);
        return "infrastructure/set-ip";
    }

    @RequestMapping(value = "/vm/{vmId}/new-ip", method = RequestMethod.POST)
    public String setVMIPAddress(@Valid @ModelAttribute(value = "vm") ChangeIpAddressData vm, BindingResult result,
                                 @ModelAttribute @PathVariable Long vmId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-ip";
        }

        VirtualMachine virtualMachine = virtualMachineRepository.findOne(vmId);
        Hypervisor vmsHypervisor = getVirtualMachineHypervisorIfAllowed(principal, virtualMachine);
        virtualMachine.setIpAddress(vm.getIpAddress());
        VirtualMachine savedVirtualMachine = virtualMachineRepository.save(virtualMachine);
        vmsHypervisor.getVirtualMachines().add(savedVirtualMachine);
        hypervisorRepository.save(vmsHypervisor);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/vm/{vmId}/new-child", method = RequestMethod.GET)
    public String createApplicationServerView(@ModelAttribute(value = "appServer") ApplicationServer appServer,
                                              @ModelAttribute @PathVariable Long vmId, Principal principal, Model model) {
        getVirtualMachineIfAllowed(principal, vmId);
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

        VirtualMachine virtualMachine = getVirtualMachineIfAllowed(principal, vmId);
        ApplicationServer savedAppServer = applicationServerRepository.save(appServer);
        virtualMachine.getApplicationServers().add(savedAppServer);
        virtualMachineRepository.save(virtualMachine);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/appServer/{appServerId}/new-port", method = RequestMethod.GET)
    public String setAppServerPortView(@ModelAttribute(value = "appServer") ChangePortData appServer,
                                       @ModelAttribute @PathVariable Long appServerId, Principal principal) {
        getApplicationServerIfAllowed(principal, appServerId);
        return "infrastructure/set-port-appserver";
    }

    @RequestMapping(value = "/appServer/{appServerId}/new-port", method = RequestMethod.POST)
    public String setAppServerPort(@Valid @ModelAttribute(value = "appServer") ChangePortData appServer, BindingResult result,
                                   @ModelAttribute @PathVariable Long appServerId, Principal principal) {
        if (result.hasErrors()) {
            return "infrastructure/set-port-appserver";
        }

        ApplicationServer appServerNode = getApplicationServerIfAllowed(principal, appServerId);
        appServerNode.setJmxPort(appServer.getPort());
        applicationServerRepository.save(appServerNode);
        return "redirect:/infrastructure";
    }

    @RequestMapping(value = "/appServer/{appServerId}", method = RequestMethod.GET)
    public String removeApplicationServer(@PathVariable Long appServerId, Principal principal) {
        ApplicationServer appServerNode = getApplicationServerIfAllowed(principal, appServerId);
        applicationServerRepository.deleteWithSubtree(appServerNode);
        return "redirect:/infrastructure";
    }

    private ServerNode getServerNodeIfAllowed(Principal principal, Long serverId) {
        User user = userRepository.findByLogin(principal.getName());
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());
        ServerNode serverNode = serverNodeRepository.findOne(serverId);

        if (!userServerNodes.contains(serverNode)) {
            throw new AccessDeniedException("User: " + principal.getName() + ", serverNode: " + serverId);
        }
        return serverNode;
    }

    private Hypervisor getHypervisorIfAllowed(Principal principal, Long hypervisorId) {
        User user = userRepository.findByLogin(principal.getName());
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());
        Hypervisor hypervisorNode = hypervisorRepository.findOne(hypervisorId);

        //TODO can do better
        boolean allowed = false;
        for (ServerNode serverNode : userServerNodes) {
            Collection<Hypervisor> serversHypervisors = template.fetch(serverNode.getHypervisors());
            if (serversHypervisors.contains(hypervisorNode)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new AccessDeniedException("User: " + principal.getName() + ", hypervisor: " + hypervisorId);
        }
        return hypervisorNode;
    }

    private Hypervisor getVirtualMachineHypervisorIfAllowed(Principal principal, VirtualMachine virtualMachine) {
        User user = userRepository.findByLogin(principal.getName());
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());

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
            throw new AccessDeniedException("User: " + principal.getName() + ", virtualMachine: " + virtualMachine.getId());
        }
        return vmsHypervisor;
    }

    private VirtualMachine getVirtualMachineIfAllowed(Principal principal, Long vmId) {
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
            throw new AccessDeniedException("User: " + principal.getName() + ", virtualMachine: " + vmId);
        }
        return virtualMachine;
    }

    private ApplicationServer getApplicationServerIfAllowed(Principal principal, Long appServerId) {
        User user = userRepository.findByLogin(principal.getName());
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());
        ApplicationServer appServerNode = applicationServerRepository.findOne(appServerId);

        //TODO can do better
        boolean allowed = false;
        for (ServerNode serverNode : userServerNodes) {
            Collection<Hypervisor> serversHypervisors = template.fetch(serverNode.getHypervisors());
            for (Hypervisor hypervisor : serversHypervisors) {
                Collection<VirtualMachine> hypervisorsVMs = template.fetch(hypervisor.getVirtualMachines());
                for (VirtualMachine vm : hypervisorsVMs) {
                    Collection<ApplicationServer> applicationServers = template.fetch(vm.getApplicationServers());
                    if (applicationServers.contains(appServerNode)) {
                        allowed = true;
                        break;
                    }
                }
            }
        }
        if (!allowed) {
            throw new AccessDeniedException("User: " + principal.getName() + ", appServer: " + appServerId);
        }
        return appServerNode;
    }
}
