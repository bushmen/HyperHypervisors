package pl.edu.agh.student.hyperhypervisors.web.dto;

import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;

public class HypervisorData extends InfrastructureObjectData<Hypervisor, VirtualMachineData> {

    public HypervisorData() {
        super(InfrastructureType.hypervisor);
    }
}
