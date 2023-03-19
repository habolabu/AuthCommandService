package edu.ou.authcommandservice.listener.account;

import edu.ou.authcommandservice.common.mapper.AccountEntityMapper;
import edu.ou.authcommandservice.data.entity.*;
import edu.ou.coreservice.common.constant.PermissionType;
import edu.ou.coreservice.listener.IBaseListener;
import edu.ou.coreservice.queue.auth.external.account.AccountAddQueueE;
import edu.ou.coreservice.queue.auth.internal.account.AccountAddQueueI;
import edu.ou.coreservice.queue.auth.internal.password.PasswordAddQueueI;
import edu.ou.coreservice.queue.auth.internal.password.PasswordDeleteByAccountIdQueueI;
import edu.ou.coreservice.repository.base.IBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("accountAddListener")
@RequiredArgsConstructor
public class AccountAddListener implements IBaseListener<Object, Object> {
    private final IBaseRepository<AccountEntity, Integer> accountAddRepository;
    private final IBaseRepository<PasswordEntity, Integer> passwordAddRepository;
    private final IBaseRepository<Integer, Integer> passwordDeleteByAccountIdRepository;
    private final IBaseRepository<String, List<PermissionEntity>> permissionFindAllByTypeRepository;
    private final IBaseRepository<List<AccountSettingEntity>, List<AccountSettingEntityPK>>
            accountSettingSaveAllRepository;
    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    /**
     * Add new account
     *
     * @param account account information
     * @return account id
     */
    @Override
    @RabbitListener(queues = AccountAddQueueE.QUEUE)
    public Object execute(Object account) {
        final Map<String, Object> dataMap = (HashMap<String, Object>)
                messageConverter.fromMessage((org.springframework.amqp.core.Message) account);
        final AccountEntity accountEntity = AccountEntityMapper.INSTANCE.fromMap(dataMap);

        final int accountId = accountAddRepository.execute(accountEntity);
        accountEntity.setId(accountId);

        this.deletePasswordByAccountId(accountId);
        this.saveDefaultPasswordForNewAccount(accountId);
        final List<AccountSettingEntity> accountSettingEntities =
                this.initDefaultPermission(accountId, accountEntity.getRoleId());

        accountEntity.setAccountSettingEntities(accountSettingEntities);

        rabbitTemplate.convertSendAndReceive(
                AccountAddQueueI.EXCHANGE,
                AccountAddQueueI.ROUTING_KEY,
                accountEntity
        );
        return accountId;
    }

    /**
     * Delete password by account id
     *
     * @param accountId account id
     */
    private void deletePasswordByAccountId(int accountId) {
        passwordDeleteByAccountIdRepository.execute(accountId);

        rabbitTemplate.convertSendAndReceive(
                PasswordDeleteByAccountIdQueueI.EXCHANGE,
                PasswordDeleteByAccountIdQueueI.ROUTING_KEY,
                accountId
        );
    }

    /**
     * Save default password for new account
     *
     * @param accountId new account id
     */
    private void saveDefaultPasswordForNewAccount(int accountId) {
        final PasswordEntity defaultPassword = new PasswordEntity().setAccountId(accountId);
        defaultPassword.setId(passwordAddRepository.execute(defaultPassword));

        rabbitTemplate.convertSendAndReceive(
                PasswordAddQueueI.EXCHANGE,
                PasswordAddQueueI.ROUTING_KEY,
                defaultPassword
        );
    }

    /**
     * Init default permission for new account
     *
     * @param accountId account id
     */
    private List<AccountSettingEntity> initDefaultPermission(int accountId, int roleId) {
        final List<PermissionEntity> permissionEntities =
                permissionFindAllByTypeRepository.execute(PermissionType.PERMISSION);

        final List<AccountSettingEntity> accountSettingEntities = permissionEntities
                .stream()
                .map(permissionEntity -> new AccountSettingEntity()
                        .setAccountId(accountId)
                        .setPermissionId(permissionEntity.getId())
                        .setRoleId(roleId)
                        .setStatus(false))
                .toList();
        accountSettingSaveAllRepository.execute(accountSettingEntities);
        return accountSettingEntities;

    }
}
