package edu.ou.authcommandservice.repository.accountSetting;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.entity.AccountSettingEntity;
import edu.ou.authcommandservice.data.entity.AccountSettingEntityPK;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.repository.base.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class AccountSettingUpdateRepository extends BaseRepository<AccountSettingEntity, AccountSettingEntityPK> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction entityTransaction;

    /**
     * Validate input
     *
     * @param accountSetting account setting
     */
    @Override
    protected void preExecute(AccountSettingEntity accountSetting) {
        if (validValidation.isInValid(accountSetting, AccountSettingEntity.class)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "account setting"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
        entityTransaction = entityManager.getTransaction();
    }

    /**
     * Update exist account setting
     *
     * @param accountSetting account setting information
     * @return id of updated account setting
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected AccountSettingEntityPK doExecute(AccountSettingEntity accountSetting) {
        final AccountSettingEntityPK accountSettingEntityPK =
                new AccountSettingEntityPK()
                        .setAccountId(accountSetting.getAccountId())
                        .setRoleId(accountSetting.getRoleId())
                        .setPermissionId(accountSetting.getPermissionId());
        try {
            entityTransaction.begin();
            entityManager
                    .find(
                            AccountSettingEntity.class,
                            accountSettingEntityPK
                    )
                    .setStatus(accountSetting.isStatus());
            entityTransaction.commit();

            return accountSettingEntityPK;

        } catch (Exception e) {
            entityTransaction.rollback();

            throw new BusinessException(
                    CodeStatus.CONFLICT,
                    Message.Error.UPDATE_FAIL,
                    "account setting"
            );

        }
    }

    /**
     * Close connection
     *
     * @param input input of task
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void postExecute(AccountSettingEntity input) {
        if (Objects.nonNull(entityManager)) {
            entityManager.close();
        }
    }
}
