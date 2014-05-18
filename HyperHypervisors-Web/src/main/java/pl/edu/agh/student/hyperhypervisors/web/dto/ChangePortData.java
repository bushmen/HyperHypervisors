package pl.edu.agh.student.hyperhypervisors.web.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ChangePortData implements Serializable {

    @NotNull(message = "{field.nonempty}")
    @Min(value = 1, message = "{port.range}")
    @Max(value = 65536, message = "{port.range}")
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
