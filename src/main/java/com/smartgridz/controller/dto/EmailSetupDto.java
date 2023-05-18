package com.smartgridz.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is used by the controller to decode a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailSetupDto
{
    @NotEmpty(message = "Host cannot be empty")
    private String host;
    @NotEmpty
    private String port;
    @NotEmpty(message = "User name should not be empty")
    private String userName;
    @NotEmpty(message = "Password should not be empty")
    private String password;
    private String smtpAuth;
    private String smtpStartTlsEnable;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\nEmail Properties:").append("\n");
        sb.append("    host              : ").append(host).append("\n");
        sb.append("    port              : ").append(port).append("\n");
        sb.append("    userName          : ").append(userName).append("\n");
        sb.append("    password          : ").append(password).append("\n");
        sb.append("    smtpAuth          : ").append(smtpAuth).append("\n");
        sb.append("    smtpStartTlsEnable: ").append(smtpStartTlsEnable).append("\n");

        return sb.toString();
    }
}
