package edu.ou.authcommandservice.repository.permission;

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
public class PermissionCheckExistByIdRepository extends BaseRepository<Integer, Boolean> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Validate permission id
     *
     * @param permissionId id of permission
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(Integer permissionId) {
        if (validValidation.isInValid(permissionId)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "permission identity"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * Check permission exist or not by id
     *
     * @param permissionId id of permission
     * @return exist or not
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Boolean doExecute(Integer permissionId) {
        final String hqlQuery = "FROM PermissionEntity P WHERE P.id = :permissionId";

        try {
            entityManager
                    .unwrap(Session.class)
                    .createQuery(hqlQuery)
                    .setParameter(
                            "permissionId",
                            permissionId
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
     * @param permissionId input of task
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void postExecute(Integer permissionId) {
        if (Objects.nonNull(entityManager)) {
            entityManager.close();
        }
    }
}
