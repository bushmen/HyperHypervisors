package pl.edu.agh.student.hyperhypervisors.web.dto;

import pl.edu.agh.student.hyperhypervisors.model.VirtualMachineDescription;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.VirtualMachine;

public class VirtualMachineData extends InfrastructureObjectData<VirtualMachine, ApplicationServerData> {

    private VirtualMachineDescription description;

    public VirtualMachineData() {
        super(InfrastructureType.vm);
    }

    public VirtualMachineDescription getDescription() {
        return description;
    }

    public void setDescription(VirtualMachineDescription description) {
        this.description = description;
    }
}
