package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Collection;

@NodeEntity
public class Hipervisor {

    @GraphId
    private Long id;

    private String login;

    private String password;

    @RelatedTo(type = Relations.HIPERVISES)
    private Collection<VirtualMachine> virtualMachines;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
