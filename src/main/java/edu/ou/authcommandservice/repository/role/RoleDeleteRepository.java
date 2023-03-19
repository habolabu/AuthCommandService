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
import javax.persistence.EntityTransaction;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class RoleDeleteRepository extends BaseRepository<Integer, Integer> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction entityTransaction;

    /**
     * Validate role id
     *
     * @param roleId role id
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(Integer roleId) {
        if (validValidation.isInValid(roleId)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "role identity"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
        entityTransaction = entityManager.getTransaction();
    }

    /**
     * Delete exist role
     *
     * @param roleId id of role which want to delete
     * @return id of deleted role
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Integer doExecute(Integer roleId) {
        final String hqlQuery =
                "UPDATE RoleEntity R " +
                        "SET R.isDeleted = CURRENT_TIMESTAMP " +
                        "WHERE R.id = :roleId";

        try {
            entityTransaction.begin();
            final int rowChangeNumber =
                    entityManager
                            .unwrap(Session.class)
                            .createQuery(hqlQuery)
                            .setParameter(
                                    "roleId",
                                    roleId
                            )
                            .executeUpdate();
            entityTransaction.commit();

            if (rowChangeNumber == 1) {
                return roleId;
            }
            return null;

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.CONFLICT,
                    Message.Error.DELETE_FAIL,
                    "role",
                    "role identity",
                    roleId
            );

        }
    }

    /**
     * Close connection
     *
     * @param roleId input of task
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void postExecute(Integer roleId) {
        if(Objects.nonNull(entityManager)){
            entityManager.close();
        }
    }
}
