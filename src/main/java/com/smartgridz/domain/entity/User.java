package com.smartgridz.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * This class represents a user of this application.  A User can have a @Role
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String login;

    @Column(name="username", nullable=false)
    private String userName;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(name="token_date")
    private Date tokenDate;

    @Column(name="reset_password_token")
    private String resetPasswordToken;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\nUser:").append("\n");
        sb.append("    id:       ").append(id).append("\n");
        sb.append("    login:     ").append(login).append("\n");
        sb.append("    username: ").append(userName).append("\n");

        sb.append("    email:    ").append(email).append("\n");
        sb.append("    password: ").append(password).append("\n");
        if ( role != null) {
            sb.append("    role:     ").append(role.getType()).append("\n");
            sb.append("    spring role:     ").append(role.getType().getSpringAuthType()).append("\n");
        } else {
            sb.append("    role:     ").append("Role us unknown").append("\n");
        }
        if (tokenDate != null) {
            sb.append("    tokenDate:     ").append(tokenDate).append("\n");
        } else {
            sb.append("    tokenDate:     ").append("Token date currently empty").append("\n");
        }
        if (resetPasswordToken != null) {
            sb.append("    token:     ").append(resetPasswordToken).append("\n");
        } else {
            sb.append("    token:     ").append("Token currently empty").append("\n");
        }
        return sb.toString();
    }
}
