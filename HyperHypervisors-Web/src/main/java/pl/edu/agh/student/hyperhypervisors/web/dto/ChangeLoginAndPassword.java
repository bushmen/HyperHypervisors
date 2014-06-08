package pl.edu.agh.student.hyperhypervisors.web.dto;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class ChangeLoginAndPassword implements Serializable {

    @NotEmpty(message = "{field.nonempty}")
    private String login;

    @NotEmpty(message = "{field.nonempty}")
    private String password;

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
}
