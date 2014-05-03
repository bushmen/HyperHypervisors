package pl.edu.agh.student.hyperhypervisors.web.dto;

import pl.edu.agh.student.hyperhypervisors.model.ServerDescription;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;

public class ServerData extends InfrastructureObjectData<ServerNode, HypervisorData> {

    private ServerDescription description;

    public ServerData() {
        super(InfrastructureType.server);
    }

    public ServerDescription getDescription() {
        return description;
    }

    public void setDescription(ServerDescription description) {
        this.description = description;
    }
}
