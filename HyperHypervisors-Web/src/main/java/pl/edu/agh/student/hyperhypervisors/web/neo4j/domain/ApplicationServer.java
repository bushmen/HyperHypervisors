package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.AppServerType;

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

    private String jmxLogin;
    private String jmxPassword;

    @NotNull(message = "{field.nonempty}")
    private AppServerType type;

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

    public AppServerType getType() {
        return type;
    }

    public void setType(AppServerType type) {
        this.type = type;
    }

    public Collection<Application> getApplications() {
        return applications;
    }

    public void setApplications(Collection<Application> applications) {
        this.applications = applications;
    }

    @Override
    public String toString() {
        return "AppServer("
                + "name=" + getName() + ", "
                + "jmxPort=" + jmxPort + ", "
                + "jmxLogin=" + jmxLogin + ", "
                + "jmxPassword=" + jmxPassword + ", "
                + "type=" + type + ")";
    }
}
