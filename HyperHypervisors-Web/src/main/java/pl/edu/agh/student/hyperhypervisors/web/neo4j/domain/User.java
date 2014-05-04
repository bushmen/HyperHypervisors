package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Collection;

@NodeEntity
public class User {

    @GraphId
    private Long id;

    @Indexed(unique = true)
    @NotEmpty(message = "{field.nonempty}")
    private String login;

    @NotEmpty(message = "{field.nonempty}")
    private String password;

    @NotEmpty(message = "{value.required}")
    private Collection<? extends UserRole> roles;

    @RelatedTo(type = Relations.CONTROLS)
    private Collection<ServerNode> servers;

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

    public Collection<? extends UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Collection<? extends UserRole> roles) {
        this.roles = roles;
    }

    public Collection<ServerNode> getServers() {
        return servers;
    }

    public void setServers(Collection<ServerNode> servers) {
        this.servers = servers;
    }
}
