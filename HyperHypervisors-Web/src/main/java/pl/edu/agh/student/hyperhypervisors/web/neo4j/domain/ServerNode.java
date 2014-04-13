package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Collection;

@NodeEntity
public class ServerNode extends Machine {

    @GraphId
    private Long id;

    @RelatedTo(type = Relations.HOSTS)
    private Collection<Hipervisor> hipervisors;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<Hipervisor> getHipervisors() {
        return hipervisors;
    }

    public void setHipervisors(Collection<Hipervisor> hipervisors) {
        this.hipervisors = hipervisors;
    }
}
