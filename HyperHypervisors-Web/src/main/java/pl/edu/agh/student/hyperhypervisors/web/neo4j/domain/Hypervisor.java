package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@NodeEntity
public class Hypervisor extends NamedNode {

    @NotNull(message = "{field.nonempty}")
    private int port;

    @NotEmpty(message = "{field.nonempty}")
    private String login;

    @NotEmpty(message = "{field.nonempty}")
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
}
