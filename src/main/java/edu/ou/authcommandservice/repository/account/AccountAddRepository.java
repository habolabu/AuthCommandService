package edu.ou.authcommandservice.repository.account;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.entity.AccountEntity;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.repository.base.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class AccountAddRepository extends BaseRepository<AccountEntity, Integer> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Validate account entity
     *
     * @param accountEntity account entity
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(AccountEntity accountEntity) {
        if (validValidation.isInValid(accountEntity, AccountEntity.class)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "account"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * Add new account entity
     *
     * @param accountEntity account
     * @return id of account
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Integer doExecute(AccountEntity accountEntity) {
        try {
            return (Integer)
                    entityManager
                            .unwrap(Session.class)
                            .save(accountEntity);

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.CONFLICT,
                    Message.Error.ADD_FAIL,
                    "account"
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
    protected void postExecute(AccountEntity input) {
        if(Objects.nonNull(entityManager)){
            entityManager.close();
        }
    }
}
