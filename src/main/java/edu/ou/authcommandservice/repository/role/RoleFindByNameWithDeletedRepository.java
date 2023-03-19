package edu.ou.authcommandservice.repository.role;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.entity.RoleEntity;
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
public class RoleFindByNameWithDeletedRepository extends BaseRepository<String, RoleEntity> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Validate role slug
     *
     * @param roleName role slug
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
     * Find role by slug with deleted
     *
     * @param roleName role slug
     * @return role
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected RoleEntity doExecute(String roleName) {
        final String hqlQuery = "FROM RoleEntity R WHERE R.name = :roleName";

        try {
            return (RoleEntity)
                    entityManager
                            .unwrap(Session.class)
                            .createQuery(hqlQuery)
                            .setParameter(
                                    "roleName",
                                    roleName
                            )
                            .getSingleResult();

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.NOT_FOUND,
                    Message.Error.NOT_FOUND,
                    "role",
                    "role slug",
                    roleName
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
