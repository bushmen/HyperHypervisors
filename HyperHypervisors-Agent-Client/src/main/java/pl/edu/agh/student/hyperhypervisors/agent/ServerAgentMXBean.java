package pl.edu.agh.student.hyperhypervisors.agent;

import pl.edu.agh.student.hyperhypervisors.dto.ServerDescription;
import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.HypervisorModel;

import java.util.List;

public interface ServerAgentMXBean {

    List<HypervisorModel> getRunningHypervisors() throws Exception;

    ServerDescription getServerDescription() throws Exception;
}
