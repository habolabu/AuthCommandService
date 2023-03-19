package edu.ou.authcommandservice.data.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(
        name = "Password",
        schema = "AuthCommandService"
)
public class PasswordEntity implements Serializable{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Basic
    @NotBlank
    @Size(
            min = 1,
            max = 255,
            message = "Length must be in range 1 - 255"
    )
    @Column(name = "password")
    private String password = "$2a$12$1dHd0ZGdkvI0chMWs2P.a.FdAfGSW/u3MHdFezey4BAUwr.DWOLi2";

    @Basic
    @Column(name = "createdAt")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Basic
    @Min(
            value = 1,
            message = "The value must be greater or equals than 1"
    )
    @Column(name = "accountId")
    private int accountId;

    @Transient
    private String rawPassword;
}
