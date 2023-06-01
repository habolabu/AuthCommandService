package edu.ou.authcommandservice.service.accountSetting;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.entity.AccountSettingEntityPK;
import edu.ou.authcommandservice.data.entity.PermissionEntity;
import edu.ou.authcommandservice.data.pojo.request.accountSetting.AccountSettingSwitchRequest;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.constant.PermissionType;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import edu.ou.coreservice.data.pojo.response.base.IBaseResponse;
import edu.ou.coreservice.data.pojo.response.impl.SuccessPojo;
import edu.ou.coreservice.data.pojo.response.impl.SuccessResponse;
import edu.ou.coreservice.repository.base.IBaseRepository;
import edu.ou.coreservice.service.base.BaseService;
import edu.ou.coreservice.service.base.IBaseService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountSettingMultiSwitchService extends BaseService<IBaseRequest, IBaseResponse> {
    private final IBaseService<IBaseRequest, IBaseResponse> accountSettingSwitchService;
    private final IBaseRepository<Integer, PermissionEntity> permissionFindByIdRepository;
    private final IBaseRepository<Integer, List<PermissionEntity>> permissionFindByParentIdRepository;

    @Override
    protected void preExecute(IBaseRequest input) {
        // do nothing
    }

    @Override
    protected IBaseResponse doExecute(IBaseRequest request) {
        final AccountSettingSwitchRequest accountSettingSwitchRequest = (AccountSettingSwitchRequest) request;
        final PermissionEntity permissionEntity = permissionFindByIdRepository
                .execute(accountSettingSwitchRequest.getReferenceKey().getPermissionId());

        if (!PermissionType.LABEL.equals(permissionEntity.getType())) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "permission type"
            );
        }

        permissionFindByParentIdRepository
                .execute(permissionEntity.getId())
                .stream()
                .map(PermissionEntity::getId)
                .forEach(permissionId -> {
                    final AccountSettingEntityPK accountSettingEntityPK =
                            new AccountSettingEntityPK()
                                    .setAccountId(accountSettingSwitchRequest.getReferenceKey().getAccountId())
                                    .setRoleId(accountSettingSwitchRequest.getReferenceKey().getRoleId())
                                    .setPermissionId(permissionId);
                    accountSettingSwitchService.execute(
                            new AccountSettingSwitchRequest()
                                    .setStatus(accountSettingSwitchRequest.isStatus())
                                    .setReferenceKey(accountSettingEntityPK));
                });
        return new SuccessResponse<>(
                new SuccessPojo<>(
                        HttpStatus.SC_OK,
                        CodeStatus.SUCCESS,
                        Message.Success.SUCCESSFUL
                )
        );
    }

    @Override
    protected void postExecute(IBaseRequest input) {
        // do nothing
    }


}
