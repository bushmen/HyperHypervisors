package pl.edu.agh.student.hyperhypervisors.web.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ChangePasswordData implements Serializable {

    @NotNull(message = "{field.nonempty}")
    @NotEmpty(message = "{field.nonempty}")
    private String newPassword;

    @NotNull(message = "{field.nonempty}")
    @NotEmpty(message = "{field.nonempty}")
    private String newPasswordRepeated;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordRepeated() {
        return newPasswordRepeated;
    }

    public void setNewPasswordRepeated(String newPasswordRepeated) {
        this.newPasswordRepeated = newPasswordRepeated;
    }
}
