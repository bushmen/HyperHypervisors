package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.Pattern;
import java.util.Collection;

@NodeEntity
public class VirtualMachine extends NamedNode {

    @NotEmpty(message = "{field.nonempty}")
    @Pattern(regexp = "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])", message = "{ip.invalid}")
    private String ipAddress;

    @RelatedTo(type = Relations.RUNS)
    private Collection<ApplicationServer> applicationServers;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Collection<ApplicationServer> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(Collection<ApplicationServer> applicationServers) {
        this.applicationServers = applicationServers;
    }
}
