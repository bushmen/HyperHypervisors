package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Collection;

@NodeEntity
public class VirtualMachine extends Machine {

    @GraphId
    private Long id;

    @RelatedTo(type = Relations.RUNS)
    private Collection<ApplicationServer> applicationServers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<ApplicationServer> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(Collection<ApplicationServer> applicationServers) {
        this.applicationServers = applicationServers;
    }
}
