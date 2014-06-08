package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@NodeEntity
public class Hypervisor extends NamedNode {

    @NotNull(message = "{field.nonempty}")
    @Min(value = 1, message = "{port.range}")
    @Max(value = 65536, message = "{port.range}")
    private int port;

    private String login;
    private String password;

    @RelatedTo(type = Relations.HYPERVISES)
    private Collection<VirtualMachine> virtualMachines;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<VirtualMachine> getVirtualMachines() {
        return virtualMachines;
    }

    public void setVirtualMachines(Collection<VirtualMachine> virtualMachines) {
        this.virtualMachines = virtualMachines;
    }

    @Override
    public String toString() {
        return "Hypervisor("
                + "name=" + getName() + ", "
                + "port=" + port + ", "
                + "login=" + login + ", "
                + "password=" + password + ")";
    }
}
