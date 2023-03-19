package edu.ou.authcommandservice.repository.accountSetting;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.entity.AccountSettingEntity;
import edu.ou.authcommandservice.data.entity.AccountSettingEntityPK;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.repository.base.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class AccountSettingSaveAllRepository extends
        BaseRepository<List<AccountSettingEntity>, List<AccountSettingEntityPK>> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction entityTransaction;

    /**
     * Validate account setting entities
     *
     * @param accountSettingEntities account setting entities
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(List<AccountSettingEntity> accountSettingEntities) {
        if (validValidation.isInValid(accountSettingEntities)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "account settings"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
        entityTransaction = entityManager.getTransaction();
    }

    /**
     * Add new account setting entities
     *
     * @param accountSettingEntities account setting entities
     * @return id of account
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected List<AccountSettingEntityPK> doExecute(List<AccountSettingEntity> accountSettingEntities) {
        final List<AccountSettingEntityPK> accountSettingEntityPKs = new ArrayList<>();

        try {
            entityTransaction.begin();
            accountSettingEntities.forEach(accountSettingEntity -> accountSettingEntityPKs.add(
                    (AccountSettingEntityPK)
                            entityManager
                                    .unwrap(Session.class)
                                    .save(accountSettingEntity)));
            entityTransaction.commit();

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.CONFLICT,
                    Message.Error.ADD_FAIL,
                    "account"
            );
        }

        return accountSettingEntityPKs;
    }

    @Override
    protected void postExecute(List<AccountSettingEntity> accountSettingEntities) {
        if (Objects.nonNull(entityManager)) {
            entityManager.close();
        }
    }
}
