package pl.edu.agh.student.hyperhypervisors.agent;

import pl.edu.agh.student.hyperhypervisors.model.ServerDescription;

public interface ServerAgentMXBean {
    ServerDescription getServerDescription() throws Exception;
}
