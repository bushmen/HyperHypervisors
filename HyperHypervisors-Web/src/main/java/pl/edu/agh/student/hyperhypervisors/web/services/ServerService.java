package pl.edu.agh.student.hyperhypervisors.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.edu.agh.student.hyperhypervisors.dto.ServerDescription;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAndPortData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ServerData;
import pl.edu.agh.student.hyperhypervisors.web.jmx.AgentConnector;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.ServerNodeRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ServerService {

    @Autowired
    ServerNodeRepository serverNodeRepository;

    @Autowired
    UserService userService;

    @Autowired
    HypervisorService hypervisorService;

    @Autowired
    Neo4jOperations template;

    public List<ServerData> getServersData(String userName) throws Exception {
        User user = userService.findByLogin(userName);
        List<ServerData> data = new ArrayList<>();
        Collection<ServerNode> serverNodes = template.fetch(user.getServers());
        for (ServerNode serverNode : serverNodes) {
            ServerData serverData = new ServerData();
            serverData.setNode(serverNode);

            AgentConnector agentConnector = createAgentConnector(serverNode);
            ServerDescription serverDescription = agentConnector.getServerDescription();
            serverData.setDescription(serverDescription);
            serverData.setChildren(hypervisorService.getHypervisorsData(agentConnector, serverNode));
            data.add(serverData);
        }
        return data;
    }

    public ServerNode createServer(ServerNode server, String userName) throws Exception {
        ServerNode savedServerNode = serverNodeRepository.save(server);
        User user = userService.findByLogin(userName);
        userService.addServer(user, server);
        addHypervisors(savedServerNode);
        return savedServerNode;
    }

    private void addHypervisors(ServerNode serverNode) throws Exception {
        List<Hypervisor> hypervisors = createAgentConnector(serverNode).getHypervisors();
        for (Hypervisor hypervisor: hypervisors) {
            hypervisorService.createHypervisor(hypervisor, serverNode);
        }
    }

    public ServerNode getServerNodeIfAllowed(String userName, Long serverId) {
        User user = userService.findByLogin(userName);
        Collection<ServerNode> userServerNodes = template.fetch(user.getServers());
        ServerNode serverNode = serverNodeRepository.findOne(serverId);

        if (!userServerNodes.contains(serverNode)) {
            throw new AccessDeniedException("User: " + userName + ", serverNode: " + serverId);
        }
        return serverNode;
    }

    public void setIPAndPort(ChangeIpAndPortData server, Long serverId, String userName) {
        ServerNode serverNode = getServerNodeIfAllowed(userName, serverId);
        serverNode.setIpAddress(server.getIpAddress());
        serverNode.setAgentPort(server.getPort());
        serverNodeRepository.save(serverNode);
    }

    public void removeServer(Long serverId, String userName) {
        ServerNode serverNode = getServerNodeIfAllowed(userName, serverId);
        serverNodeRepository.deleteWithSubtree(serverNode);
    }

    public void addHypervisor(ServerNode serverNode, Hypervisor hypervisor) {
        serverNode.getHypervisors().add(hypervisor);
        serverNodeRepository.save(serverNode);
    }

    protected AgentConnector createAgentConnector(ServerNode serverNode) {
        return new AgentConnector(serverNode);
    }
}
