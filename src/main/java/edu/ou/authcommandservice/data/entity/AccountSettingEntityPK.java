package edu.ou.authcommandservice.data.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class AccountSettingEntityPK implements Serializable {
    @Column(name = "accountId")
    @Id
    @Min(
            value = 1,
            message = "The value must be greater or equals than 1"
    )
    private int accountId;
    @Column(name = "roleId")
    @Id
    @Min(
            value = 1,
            message = "The value must be greater or equals than 1"
    )
    private int roleId;
    @Column(name = "permissionId")
    @Id
    @Min(
            value = 1,
            message = "The value must be greater or equals than 1"
    )
    private int permissionId;
}
