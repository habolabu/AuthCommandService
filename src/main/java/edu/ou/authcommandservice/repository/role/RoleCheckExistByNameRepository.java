package edu.ou.authcommandservice.repository.role;

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
public class RoleCheckExistByNameRepository extends BaseRepository<String, Boolean> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Validate role name
     *
     * @param roleName name of role
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(String roleName) {
        if (validValidation.isInValid(roleName)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "role name"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * Check role exist or not by name
     *
     * @param roleName name of role
     * @return exist or not
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Boolean doExecute(String roleName) {
        final String hqlQuery = "FROM RoleEntity R WHERE R.name = :roleName";

        try {
            entityManager
                    .unwrap(Session.class)
                    .createQuery(hqlQuery)
                    .setParameter(
                            "roleName",
                            roleName
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
     * @param roleName input of task
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void postExecute(String roleName) {
        if (Objects.nonNull(entityManager)) {
            entityManager.close();
        }
    }
}
