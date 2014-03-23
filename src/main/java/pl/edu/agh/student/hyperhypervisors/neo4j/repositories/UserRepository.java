package pl.edu.agh.student.hyperhypervisors.neo4j.repositories;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import pl.edu.agh.student.hyperhypervisors.neo4j.domain.User;

public interface UserRepository extends GraphRepository<User>, RelationshipOperationsRepository<User> {

    User findByLogin(String login);
}
