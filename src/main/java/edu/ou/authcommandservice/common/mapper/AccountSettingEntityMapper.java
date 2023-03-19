package edu.ou.authcommandservice.common.mapper;

import edu.ou.authcommandservice.data.entity.AccountSettingEntity;
import edu.ou.authcommandservice.data.entity.AccountSettingEntityPK;
import edu.ou.authcommandservice.data.pojo.request.accountSetting.AccountSettingSwitchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountSettingEntityMapper {
    AccountSettingEntityMapper INSTANCE = Mappers.getMapper(AccountSettingEntityMapper.class);

    /**
     * Map AccountSettingSwitchRequest object to AccountSettingEntity object
     *
     * @param accountSettingSwitchRequest represents for AccountSettingSwitchRequest object
     * @return AccountSettingEntity object
     * @author Nguyen Trung Kien - OU
     */
    @Mapping(target = "accountId", source = "referenceKey", qualifiedByName = "referenceKeyToAccountId")
    @Mapping(target = "permissionId", source = "referenceKey", qualifiedByName = "referenceKeyToPermissionId")
    @Mapping(target = "roleId", source = "referenceKey", qualifiedByName = "referenceKeyToRoleId")
    @Mapping(target = "status", source = "status")
    AccountSettingEntity fromAccountSettingSwitchRequest(AccountSettingSwitchRequest accountSettingSwitchRequest);


    /**
     * Convert reference key to account setting
     *
     * @param accountSettingEntityPK key of account setting
     * @return account id of account setting
     * @author Nguyen Trung Kien - OU
     */
    @Named("referenceKeyToAccountId")
    static int toAccountId(AccountSettingEntityPK accountSettingEntityPK) {
        return accountSettingEntityPK.getAccountId();
    }

    /**
     * Convert reference key to account setting
     *
     * @param accountSettingEntityPK key of account setting
     * @return permission id of account setting
     * @author Nguyen Trung Kien - OU
     */
    @Named("referenceKeyToPermissionId")
    static int toPermissionId(AccountSettingEntityPK accountSettingEntityPK) {
        return accountSettingEntityPK.getPermissionId();
    }

    /**
     * Convert reference key to account setting
     *
     * @param accountSettingEntityPK key of account setting
     * @return role id of account setting
     * @author Nguyen Trung Kien - OU
     */
    @Named("referenceKeyToRoleId")
    static int toRoleId(AccountSettingEntityPK accountSettingEntityPK) {
        return accountSettingEntityPK.getRoleId();
    }
}
