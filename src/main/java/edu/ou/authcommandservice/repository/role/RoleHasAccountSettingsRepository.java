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
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class RoleHasAccountSettingsRepository extends BaseRepository<Integer, Boolean> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

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
    }

    /**
     * Check account setting in role
     *
     * @param roleId role id
     * @return role has account setting or not
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Boolean doExecute(Integer roleId) {
        final String hqlQuery = "SELECT R.id " +
                "FROM RoleEntity R " +
                "JOIN AccountSettingEntity ASE ON R.id = ASE.roleId " +
                "WHERE R.id = :roleId AND R.isDeleted IS NULL AND ASE.status = TRUE";

        try {
            return !entityManager
                    .unwrap(Session.class)
                    .createQuery(hqlQuery)
                    .setParameter(
                            "roleId",
                            roleId
                    )
                    .getResultList()
                    .isEmpty();

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.NOT_FOUND,
                    Message.Error.NOT_FOUND,
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
        if (Objects.nonNull(entityManager)) {
            entityManager.close();
        }
    }
}
