package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Collection;

@NodeEntity
public class VirtualMachine extends NamedNode {

    @Pattern(regexp = "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])", message = "{ip.invalid}")
    private String ipAddress;

    @Min(value = 0, message = "{port.range}")
    @Max(value = 65536, message = "{port.range}")
    private int agentPort;

    @RelatedTo(type = Relations.RUNS)
    private Collection<ApplicationServer> applicationServers;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(int agentPort) {
        this.agentPort = agentPort;
    }

    public Collection<ApplicationServer> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(Collection<ApplicationServer> applicationServers) {
        this.applicationServers = applicationServers;
    }
}
