package pl.edu.agh.student.hyperhypervisors.model;

import java.io.Serializable;
import java.util.List;

public class ServerDescription implements Serializable {

    private MemoryDescription memory;
    private List<CpuDescription> cpus;

    public MemoryDescription getMemory() {
        return memory;
    }

    public void setMemory(MemoryDescription memory) {
        this.memory = memory;
    }

    public List<CpuDescription> getCpus() {
        return cpus;
    }

    public void setCpus(List<CpuDescription> cpus) {
        this.cpus = cpus;
    }
}
