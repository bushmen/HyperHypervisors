package pl.edu.agh.student.hyperhypervisors.agent;

import java.util.List;

public interface AppServerAgentMXBean {

    List<String> getTomcatAppsNamesList(String url, String user, String password);

    List<String> getJboss6AppsNamesList(String url, String user, String password);
}
