package pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ApplicationServer;

public interface ApplicationServerRepository extends GraphRepository<ApplicationServer>, RelationshipOperationsRepository<ApplicationServer> {

    @Query(value = "start appServer=node({0}) " +
            "match ()-[rel:RUNS]->appServer " +
            "delete rel, appServer")
    void deleteWithSubtree(ApplicationServer applicationServer);
}
