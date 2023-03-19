package edu.ou.authcommandservice.repository.role;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.entity.RoleEntity;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.repository.base.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class RoleUpdateRepository extends BaseRepository<RoleEntity, Integer> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction entityTransaction;

    /**
     * validate role
     *
     * @param role role
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(RoleEntity role) {
        if (validValidation.isInValid(role, RoleEntity.class)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "role"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
        entityTransaction = entityManager.getTransaction();
    }

    /**
     * Update exist role
     *
     * @param role exist role
     * @return id of exist role
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Integer doExecute(RoleEntity role) {
        try {
            entityTransaction.begin();
            entityManager
                    .find(
                            RoleEntity.class,
                            role.getId()
                    )
                    .setName(role.getName())
                    .setDisplay(role.getDisplay())
                    .setIsDeleted(role.getIsDeleted());
            entityTransaction.commit();

            return role.getId();

        } catch (Exception e) {
            entityTransaction.rollback();

            throw new BusinessException(
                    CodeStatus.CONFLICT,
                    Message.Error.UPDATE_FAIL,
                    "role"
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
    protected void postExecute(RoleEntity input) {
        if (Objects.nonNull(entityManager)) {
            entityManager.close();
        }
    }
}
