package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Collection;

@NodeEntity
public class ServerNode extends NamedNode {

    @NotEmpty(message = "{field.nonempty}")
    @Pattern(regexp = "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])", message = "{ip.invalid}")
    private String ipAddress;

    @NotNull(message = "{field.nonempty}")
    @Min(value = 1, message = "{port.range}")
    @Max(value = 65536, message = "{port.range}")
    private int agentPort;

    @NotEmpty(message = "{field.nonempty}")
    private String agentLogin;

    @NotEmpty(message = "{field.nonempty}")
    private String agentPassword;

    @RelatedTo(type = Relations.HOSTS)
    private Collection<Hypervisor> hypervisors;

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

    public String getAgentLogin() {
        return agentLogin;
    }

    public void setAgentLogin(String agentLogin) {
        this.agentLogin = agentLogin;
    }

    public String getAgentPassword() {
        return agentPassword;
    }

    public void setAgentPassword(String agentPassword) {
        this.agentPassword = agentPassword;
    }

    public Collection<Hypervisor> getHypervisors() {
        return hypervisors;
    }

    public void setHypervisors(Collection<Hypervisor> hypervisors) {
        this.hypervisors = hypervisors;
    }

    @Override
    public String toString() {
        return "Server("
                + "name=" + getName() + ", "
                + "ipAddress=" + ipAddress + ", "
                + "agentPort=" + agentPort + ", "
                + "agentLogin=" + agentLogin + ", "
                + "agentPassword=" + agentPassword + ", "
                + "hypervisors=" + hypervisors + ")";
    }
}
