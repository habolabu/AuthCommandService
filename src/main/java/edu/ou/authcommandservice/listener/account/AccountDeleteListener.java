package edu.ou.authcommandservice.listener.account;

import edu.ou.authcommandservice.data.entity.AccountEntity;
import edu.ou.coreservice.listener.IBaseListener;
import edu.ou.coreservice.queue.auth.external.account.AccountDeleteQueueE;
import edu.ou.coreservice.queue.auth.internal.account.AccountDeleteQueueI;
import edu.ou.coreservice.queue.auth.internal.password.PasswordDeleteByAccountIdQueueI;
import edu.ou.coreservice.repository.base.IBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component("accountDeleteListener")
@RequiredArgsConstructor
public class AccountDeleteListener implements IBaseListener<Integer, Object> {
    private final IBaseRepository<Integer, Integer> accountDeleteRepository;
    private final IBaseRepository<Integer, AccountEntity> accountFindByUserIdRepository;
    private final IBaseRepository<Integer, Integer> passwordDeleteByAccountIdRepository;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Delete account
     *
     * @param userId user id
     * @return accout id
     */
    @Override
    @RabbitListener(queues = AccountDeleteQueueE.QUEUE)
    public Object execute(Integer userId) {
        final AccountEntity account = accountFindByUserIdRepository.execute(userId);

        this.deletePasswordByAccountId(account.getId());

        final int accountId = accountDeleteRepository.execute(userId);

        rabbitTemplate.convertSendAndReceive(
                AccountDeleteQueueI.EXCHANGE,
                AccountDeleteQueueI.ROUTING_KEY,
                accountId
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
}
