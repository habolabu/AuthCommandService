package edu.ou.authcommandservice.data.pojo.request.accountSetting;

import edu.ou.authcommandservice.data.entity.AccountSettingEntityPK;
import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AccountSettingSwitchRequest implements IBaseRequest {
    @NotNull
    private AccountSettingEntityPK referenceKey;
    private boolean status;
}
