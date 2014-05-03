package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Collection;

@NodeEntity
public class ServerNode extends Machine {

    @RelatedTo(type = Relations.HOSTS)
    private Collection<Hypervisor> hypervisors;

    public Collection<Hypervisor> getHypervisors() {
        return hypervisors;
    }

    public void setHypervisors(Collection<Hypervisor> hypervisors) {
        this.hypervisors = hypervisors;
    }
}
