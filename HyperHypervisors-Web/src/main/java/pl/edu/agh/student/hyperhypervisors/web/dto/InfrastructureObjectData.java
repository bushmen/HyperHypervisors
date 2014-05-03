package pl.edu.agh.student.hyperhypervisors.web.dto;

import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.NamedNode;

import java.io.Serializable;
import java.util.List;

public abstract class InfrastructureObjectData<Node extends NamedNode, Child> implements Serializable {

    private Long id;
    private InfrastructureType type;
    private String text;
    private Node node;
    private List<Child> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    protected InfrastructureObjectData(InfrastructureType type) {
        this.type = type;
    }

    public InfrastructureType getType() {
        return type;
    }

    public void setType(InfrastructureType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        setId(node.getId());
        setText(node.getName());
        this.node = node;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }
}
