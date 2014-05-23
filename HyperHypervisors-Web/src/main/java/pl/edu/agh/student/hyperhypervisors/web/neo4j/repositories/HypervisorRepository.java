package pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;

public interface HypervisorRepository extends GraphRepository<Hypervisor>, RelationshipOperationsRepository<Hypervisor> {

    @Query(value = "start hypervisor=node({0}) " +
            "match ()-[rel:HOSTS]->hypervisor " +
            "optional match hypervisor-[rels*]->(a) " +
            "foreach(r in rels | delete r) " +
            "delete a, rel, hypervisor")
    void deleteWithSubtree(Hypervisor hypervisor);
}
