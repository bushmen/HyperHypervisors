package pl.edu.agh.student.hyperhypervisors.web.dto;

import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ApplicationServer;

public class ApplicationServerData extends InfrastructureObjectData<ApplicationServer, ApplicationData> {

    public ApplicationServerData() {
        super(InfrastructureType.appServer);
    }
}
