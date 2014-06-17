package pl.edu.agh.student.hyperhypervisors.agent;

import pl.edu.agh.student.hyperhypervisors.dto.ServerDescription;
import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.HypervisorModel;

import java.util.List;

/**
 * This MBean is used to retrieve information about condition of server on which agent is running and to find hypervisors running on this server
 */

public interface ServerAgentMXBean {

    List<HypervisorModel> getRunningHypervisors() throws Exception;

    ServerDescription getServerDescription() throws Exception;
}
