package edu.ou.authcommandservice.service.password;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.common.mapper.PasswordEntityMapper;
import edu.ou.authcommandservice.common.util.KeyCloakUtils;
import edu.ou.authcommandservice.data.entity.AccountEntity;
import edu.ou.authcommandservice.data.entity.PasswordEntity;
import edu.ou.authcommandservice.data.pojo.request.password.PasswordAddRequest;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import edu.ou.coreservice.data.pojo.response.base.IBaseResponse;
import edu.ou.coreservice.data.pojo.response.impl.SuccessPojo;
import edu.ou.coreservice.data.pojo.response.impl.SuccessResponse;
import edu.ou.coreservice.queue.auth.internal.password.PasswordAddQueueI;
import edu.ou.coreservice.queue.auth.internal.password.PasswordDeleteByAccountIdQueueI;
import edu.ou.coreservice.queue.human.external.user.UserFindDetailByIdQueueE;
import edu.ou.coreservice.repository.base.IBaseRepository;
import edu.ou.coreservice.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordAddService extends BaseService<IBaseRequest, IBaseResponse> {
    private final IBaseRepository<PasswordEntity, Integer> passwordAddRepository;
    private final IBaseRepository<String, AccountEntity> accountLoadByUsernameRepository;
    private final IBaseRepository<Integer, Integer> passwordDeleteByAccountIdRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ValidValidation validValidation;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Keycloak keyCloakInstance;
    @Value("${keycloak.realm}")
    private String realm;

    /**
     * Validate request
     *
     * @param request input of task
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(IBaseRequest request) {
        if (validValidation.isInValid(request, PasswordAddRequest.class)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "new password"
            );
        }
    }

    /**
     * Add new password
     *
     * @param request request from client
     * @return response to client
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected IBaseResponse doExecute(IBaseRequest request) {
        final UserDetails principal = (UserDetails)
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
        final AccountEntity account = accountLoadByUsernameRepository.execute(
                String.valueOf(principal.getUsername()));
        final PasswordEntity passwordEntity = PasswordEntityMapper.INSTANCE
                .fromPasswordAddRequest((PasswordAddRequest) request);
        passwordEntity.setAccountId(account.getId());

        passwordDeleteByAccountIdRepository.execute(passwordEntity.getAccountId());
        rabbitTemplate.convertSendAndReceive(
                PasswordDeleteByAccountIdQueueI.EXCHANGE,
                PasswordDeleteByAccountIdQueueI.ROUTING_KEY,
                passwordEntity.getAccountId()
        );

        passwordEntity.setPassword(bCryptPasswordEncoder.encode(passwordEntity.getRawPassword()));
        final int passwordId = passwordAddRepository.execute(passwordEntity);
        passwordEntity.setId(passwordId);
        rabbitTemplate.convertSendAndReceive(
                PasswordAddQueueI.EXCHANGE,
                PasswordAddQueueI.ROUTING_KEY,
                passwordEntity
        );

        final Object userInfo = rabbitTemplate.convertSendAndReceive(
                UserFindDetailByIdQueueE.EXCHANGE,
                UserFindDetailByIdQueueE.ROUTING_KEY,
                account.getUserId()
        );

        final Map<String, Object> userInfoData = (Map<String, Object>) userInfo;

        assert userInfoData != null;
        this.savePasswordToKeyCloak(
                passwordEntity.getRawPassword(),
                (String) userInfoData.get("keyCloakId")
        );

        return new SuccessResponse<>(
                new SuccessPojo<>(
                        passwordId,
                        CodeStatus.SUCCESS,
                        Message.Success.SUCCESSFUL
                )
        );
    }

    @Override
    protected void postExecute(IBaseRequest input) {
        // do nothing
    }

    /**
     * Update new password to keycloak
     *
     * @param newPassword password
     * @param keycloakId  keycloak id
     */
    private void savePasswordToKeyCloak(
            String newPassword,
            String keycloakId
    ) {
        KeyCloakUtils.changePassword(
                keyCloakInstance,
                realm,
                keycloakId,
                newPassword
        );
    }
}
