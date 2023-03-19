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
import javax.persistence.EntityTransaction;
import java.util.Objects;

@Repository("accountDeleteRepository")
@RequiredArgsConstructor
public class AccountDeleteRepository extends BaseRepository<Integer, Integer> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction entityTransaction;

    /**
     * Validate user id
     *
     * @param userId user id
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(Integer userId) {
        if (validValidation.isInValid(userId)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "user identity"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
        entityTransaction = entityManager.getTransaction();
    }

    /**
     * Delete exist account by user id
     *
     * @param userId account of user id which want to delete
     * @return account id
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Integer doExecute(Integer userId) {
        final String hqlQuery = "DELETE FROM AccountEntity A WHERE A.userId = :userId";

        try {
            entityTransaction.begin();
            final int rowChangeNumber =
                    entityManager
                            .unwrap(Session.class)
                            .createQuery(hqlQuery)
                            .setParameter(
                                    "userId",
                                    userId
                            )
                            .executeUpdate();
            entityTransaction.commit();

            if (rowChangeNumber == 1) {
                return userId;
            }
            return null;

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.CONFLICT,
                    Message.Error.DELETE_FAIL,
                    "account",
                    "user identity",
                    userId.toString()
            );

        }
    }

    /**
     * Close connection
     *
     * @param userId input of task
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void postExecute(Integer userId) {
        if(Objects.nonNull(entityManager)){
            entityManager.close();
        }
    }
}
