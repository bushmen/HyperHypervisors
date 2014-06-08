package pl.edu.agh.student.hyperhypervisors.dto.infrastructure;

import java.io.Serializable;
import java.util.Collection;

public class ServerModel implements Serializable {

    private String name;
    private String ipAddress;
    private int agentPort;
    private String agentLogin;
    private String agentPassword;
    private Collection<HypervisorModel> hypervisors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Collection<HypervisorModel> getHypervisors() {
        return hypervisors;
    }

    public void setHypervisors(Collection<HypervisorModel> hypervisors) {
        this.hypervisors = hypervisors;
    }
}
