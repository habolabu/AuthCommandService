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
public class RoleAddRepository extends BaseRepository<RoleEntity, Integer> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Validate role
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
    }

    /**
     * Add new role
     *
     * @param role new role
     * @return id of new role
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Integer doExecute(RoleEntity role) {
        try {
            return (Integer)
                    entityManager
                            .unwrap(Session.class)
                            .save(role);

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.CONFLICT,
                    Message.Error.ADD_FAIL,
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
        if(Objects.nonNull(entityManager)){
            entityManager.close();
        }
    }
}
