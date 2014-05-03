package pl.edu.agh.student.hyperhypervisors.web.dto;

import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Application;

import java.util.List;

public class ApplicationData extends InfrastructureObjectData<Application, Object> {

    public ApplicationData() {
        super(InfrastructureType.app);
    }

    @Override
    public List<Object> getChildren() {
        return null;
    }

    @Override
    public void setChildren(List<Object> children) {

    }
}
