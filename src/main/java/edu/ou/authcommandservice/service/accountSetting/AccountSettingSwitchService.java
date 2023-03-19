package edu.ou.authcommandservice.service.accountSetting;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.common.mapper.AccountSettingEntityMapper;
import edu.ou.authcommandservice.data.entity.AccountSettingEntity;
import edu.ou.authcommandservice.data.entity.AccountSettingEntityPK;
import edu.ou.authcommandservice.data.pojo.request.accountSetting.AccountSettingSwitchRequest;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import edu.ou.coreservice.data.pojo.response.base.IBaseResponse;
import edu.ou.coreservice.data.pojo.response.impl.SuccessPojo;
import edu.ou.coreservice.data.pojo.response.impl.SuccessResponse;
import edu.ou.coreservice.queue.auth.internal.accountSetting.AccountSettingUpdateQueueI;
import edu.ou.coreservice.repository.base.IBaseRepository;
import edu.ou.coreservice.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountSettingSwitchService extends BaseService<IBaseRequest, IBaseResponse> {
    private final IBaseRepository<AccountSettingEntity, AccountSettingEntityPK> accountSettingUpdateRepository;
    private final IBaseRepository<Integer, Boolean> accountCheckExistByIdRepository;
    private final IBaseRepository<Integer, Boolean> permissionCheckExistByIdRepository;
    private final IBaseRepository<Integer, Boolean> roleCheckExistByIdRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ValidValidation validValidation;

    /**
     * Validate input
     *
     * @param request request
     */
    @Override
    protected void preExecute(IBaseRequest request) {
        if (validValidation.isInValid(request, AccountSettingSwitchRequest.class)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "account setting"
            );
        }
    }

    /**
     * Switch status of account setting
     *
     * @param request account setting
     * @return account setting keu
     */
    @Override
    protected IBaseResponse doExecute(IBaseRequest request) {
        final AccountSettingEntity accountSetting = AccountSettingEntityMapper.INSTANCE
                .fromAccountSettingSwitchRequest((AccountSettingSwitchRequest) request);

        if (!accountCheckExistByIdRepository.execute(accountSetting.getAccountId())
                || !permissionCheckExistByIdRepository.execute(accountSetting.getPermissionId())
                || !roleCheckExistByIdRepository.execute(accountSetting.getRoleId())) {
            throw new BusinessException(
                    CodeStatus.NOT_FOUND,
                    Message.Error.NOT_FOUND,
                    "account, permission, role",
                    "account identity, permission identity, role identity",
                    String.format("%d, %d, %d",
                            accountSetting.getAccountId(),
                            accountSetting.getPermissionId(),
                            accountSetting.getRoleId())
            );
        }

        final AccountSettingEntityPK accountSettingEntityPK = accountSettingUpdateRepository.execute(accountSetting);

        rabbitTemplate.convertSendAndReceive(
                AccountSettingUpdateQueueI.EXCHANGE,
                AccountSettingUpdateQueueI.ROUTING_KEY,
                accountSetting
        );

        return new SuccessResponse<>(
                new SuccessPojo<>(
                        accountSettingEntityPK,
                        CodeStatus.SUCCESS,
                        Message.Success.SUCCESSFUL
                )
        );
    }

    @Override
    protected void postExecute(IBaseRequest request) {
        // do nothing
    }
}
