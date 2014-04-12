package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Collection;

@NodeEntity
public class ApplicationServer {

    @GraphId
    private Long id;

    private int jmxPort;

    private String jmxLogin;

    private String jmxPassword;

    @RelatedTo(type = Relations.DISTRIBUTES)
    private Collection<Application> applications;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
