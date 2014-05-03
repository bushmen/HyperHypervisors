package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Collection;

@NodeEntity
public class VirtualMachine extends Machine {

    @RelatedTo(type = Relations.RUNS)
    private Collection<ApplicationServer> applicationServers;

    public Collection<ApplicationServer> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(Collection<ApplicationServer> applicationServers) {
        this.applicationServers = applicationServers;
    }
}
