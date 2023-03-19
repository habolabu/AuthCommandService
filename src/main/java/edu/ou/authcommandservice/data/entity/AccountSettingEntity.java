package edu.ou.authcommandservice.data.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Entity
@Data
@Table(
        name = "AccountSetting",
        schema = "AuthCommandService"
)
@IdClass(AccountSettingEntityPK.class)
public class AccountSettingEntity implements Serializable {
    @Id
    @Min(
            value = 1,
            message = "The value must be greater or equals than 1"
    )
    @Column(name = "accountId")
    private int accountId;
    @Id
    @Min(
            value = 1,
            message = "The value must be greater or equals than 1"
    )
    @Column(name = "roleId")
    private int roleId;
    @Id
    @Min(
            value = 1,
            message = "The value must be greater or equals than 1"
    )
    @Column(name = "permissionId")
    private int permissionId;
    @Basic
    @Column(name = "status")
    private boolean status = true;
}
