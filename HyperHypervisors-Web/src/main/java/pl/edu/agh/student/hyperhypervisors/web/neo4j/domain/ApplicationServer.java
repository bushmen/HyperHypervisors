package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@NodeEntity
public class ApplicationServer extends NamedNode {

    @NotNull(message = "{field.nonempty}")
    @Min(value = 1, message = "{port.range}")
    @Max(value = 65535, message = "{port.range}")
    private int jmxPort;

    @NotEmpty(message = "{field.nonempty}")
    private String jmxLogin;

    @NotEmpty(message = "{field.nonempty}")
    private String jmxPassword;

    @RelatedTo(type = Relations.DISTRIBUTES)
    private Collection<Application> applications;

    public int getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(int jmxPort) {
        this.jmxPort = jmxPort;
    }

    public String getJmxLogin() {
        return jmxLogin;
    }

    public void setJmxLogin(String jmxLogin) {
        this.jmxLogin = jmxLogin;
    }

    public String getJmxPassword() {
        return jmxPassword;
    }

    public void setJmxPassword(String jmxPassword) {
        this.jmxPassword = jmxPassword;
    }

    public Collection<Application> getApplications() {
        return applications;
    }

    public void setApplications(Collection<Application> applications) {
        this.applications = applications;
    }
}
