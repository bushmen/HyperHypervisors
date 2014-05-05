package pl.edu.agh.student.hyperhypervisors.web.neo4j.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.GraphId;

public abstract class NamedNode {

    @GraphId
    private Long id;

    @NotEmpty(message = "{field.nonempty}")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof NamedNode)) {
            return false;
        }

        NamedNode namedNode = (NamedNode) o;
        return (id == null && namedNode.id == null) || (id != null && id.equals(namedNode.id));
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}