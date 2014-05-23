package pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.VirtualMachine;

public interface VirtualMachineRepository extends GraphRepository<VirtualMachine>, RelationshipOperationsRepository<VirtualMachine> {

    @Query(value = "start vm=node({0}) " +
            "match ()-[rel:HYPERVISES]->vm " +
            "optional match vm-[rels*]->(a) " +
            "foreach(r in rels | delete r) " +
            "delete a, rel, vm")
    void deleteWithSubtree(VirtualMachine virtualMachine);
}
