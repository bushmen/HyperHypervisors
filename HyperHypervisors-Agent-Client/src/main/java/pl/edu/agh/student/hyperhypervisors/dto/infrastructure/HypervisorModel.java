package pl.edu.agh.student.hyperhypervisors.dto.infrastructure;

import java.io.Serializable;

public class HypervisorModel implements Serializable {

    private String name;
    private int port;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}