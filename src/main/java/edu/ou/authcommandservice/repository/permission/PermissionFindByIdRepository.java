package edu.ou.authcommandservice.repository.permission;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.entity.PermissionEntity;
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
public class PermissionFindByIdRepository extends BaseRepository<Integer, PermissionEntity> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Validate permission id
     *
     * @param permissionId permission id
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
     * Find permission by permission id with deleted
     *
     * @param permissionId permission id
     * @return permission
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected PermissionEntity doExecute(Integer permissionId) {
        final String hqlQuery = "FROM PermissionEntity P WHERE P.id = :permissionId";

        try {
            return (PermissionEntity)
                    entityManager
                            .unwrap(Session.class)
                            .createQuery(hqlQuery)
                            .setParameter(
                                    "permissionId",
                                    permissionId
                            )
                            .getSingleResult();

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.NOT_FOUND,
                    Message.Error.NOT_FOUND,
                    "permission",
                    "permission identity",
                    permissionId.toString()
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
    protected void postExecute(Integer input) {
        if(Objects.nonNull(entityManager)){
            entityManager.close();
        }
    }
}
