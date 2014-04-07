package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@NodeEntity
public class User {

    @GraphId
    private Long id;

    @Indexed(unique = true)
    @NotNull(message = "{field.nonempty}")
    @NotEmpty(message = "{field.nonempty}")
    private String login;

    @NotNull(message = "{field.nonempty}")
    @NotEmpty(message = "{field.nonempty}")
    private String password;

    @NotNull(message = "{value.required}")
    @Size(min = 1, message = "{value.required}")
    private Collection<? extends UserRole> roles;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        if (id == null) {
            return super.equals(o);
        }
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
}
