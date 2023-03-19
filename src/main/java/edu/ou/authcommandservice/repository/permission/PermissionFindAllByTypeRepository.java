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
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class PermissionFindAllByTypeRepository extends BaseRepository<String, List<PermissionEntity>> {
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private final ValidValidation validValidation;

    /***
     * Init entity manager
     *
     * @param type type
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(String type) {
        if (validValidation.isInValid(type)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "permission type"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * Find all permissions by type
     *
     * @param type type
     * @return list of permission
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected List<PermissionEntity> doExecute(String type) {
        final String hqlQuery = "FROM PermissionEntity P WHERE P.type = :type";
        return entityManager
                .unwrap(Session.class)
                .createQuery(hqlQuery)
                .setParameter(
                        "type",
                        type
                )
                .getResultList();
    }

    /**
     * Close connection
     *
     * @param input input of task
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void postExecute(String input) {
        if (Objects.nonNull(entityManager)) {
            entityManager.close();
        }
    }
}
