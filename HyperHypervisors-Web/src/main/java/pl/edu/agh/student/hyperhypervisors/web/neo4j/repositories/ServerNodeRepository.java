package pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;

public interface ServerNodeRepository extends GraphRepository<ServerNode>, RelationshipOperationsRepository<ServerNode> {
}
