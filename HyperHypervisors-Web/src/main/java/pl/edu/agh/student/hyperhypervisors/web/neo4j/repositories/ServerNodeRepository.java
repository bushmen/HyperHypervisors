package pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;

public interface ServerNodeRepository extends GraphRepository<ServerNode>, RelationshipOperationsRepository<ServerNode> {

    @Query(value = "start server=node({0}) " +
            "match ()-[rel:CONTROLS]->server " +
            "optional match server-[rels*]->(a) " +
            "foreach(r in rels | delete r) " +
            "delete a, rel, server")
    void deleteWithSubtree(ServerNode serverNode);
}
