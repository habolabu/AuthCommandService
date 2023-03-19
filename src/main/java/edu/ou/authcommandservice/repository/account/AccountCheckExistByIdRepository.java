package edu.ou.authcommandservice.repository.account;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.repository.base.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class AccountCheckExistByIdRepository extends BaseRepository<Integer, Boolean> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Validate account id
     *
     * @param accountId id of account
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(Integer accountId) {
        if (validValidation.isInValid(accountId)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "account identity"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * Check account exist or not by id
     *
     * @param accountId id of account
     * @return exist or not
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Boolean doExecute(Integer accountId) {
        final String hqlQuery = "FROM AccountEntity A WHERE A.id = :accountId";

        try {
            entityManager
                    .unwrap(Session.class)
                    .createQuery(hqlQuery)
                    .setParameter(
                            "accountId",
                            accountId
                    )
                    .getSingleResult();
            return true;

        } catch (NoResultException e) {
            return false;

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.SERVER_ERROR,
                    Message.Error.UN_KNOWN
            );

        }
    }

    /**
     * Close connection
     *
     * @param accountId input of task
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void postExecute(Integer accountId) {
        if (Objects.nonNull(entityManager)) {
            entityManager.close();
        }
    }
}
