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

@Repository("accountLoadByUsernameRepository")
@RequiredArgsConstructor
public class AccountLoadByUsernameRepository extends BaseRepository<String, AccountEntity> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Validate username
     *
     * @param username username
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(String username) {
        if (validValidation.isInValid(username)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "username"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * Find account by username with deleted
     *
     * @param username username
     * @return account
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected AccountEntity doExecute(String username) {
        final String hqlQuery = "FROM AccountEntity A WHERE A.username = :username";

        try {
            return (AccountEntity)
                    entityManager
                            .unwrap(Session.class)
                            .createQuery(hqlQuery)
                            .setParameter(
                                    "username",
                                    username
                            )
                            .getSingleResult();

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.CONFLICT,
                    Message.Error.NOT_FOUND,
                    "account",
                    "username",
                    username
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
    protected void postExecute(String input) {
        if(Objects.nonNull(entityManager)){
            entityManager.close();
        }
    }
}
