package edu.ou.authcommandservice.repository.password;


import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.entity.PasswordEntity;
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

@Repository("passwordAddRepository")
@RequiredArgsConstructor
public class PasswordAddRepository extends BaseRepository<PasswordEntity, Integer> {
    private final ValidValidation validValidation;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Validate password entity
     *
     * @param passwordEntity password entity
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(PasswordEntity passwordEntity) {
        if (validValidation.isInValid(passwordEntity, PasswordEntity.class)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "password"
            );
        }
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * Add new password entity
     *
     * @param passwordEntity password
     * @return id of password
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Integer doExecute(PasswordEntity passwordEntity) {
        try {
            return (Integer)
                    entityManager
                            .unwrap(Session.class)
                            .save(passwordEntity);

        } catch (Exception e) {
            throw new BusinessException(
                    CodeStatus.CONFLICT,
                    Message.Error.ADD_FAIL,
                    "password"
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
    protected void postExecute(PasswordEntity input) {
        if(Objects.nonNull(entityManager)){
            entityManager.close();
        }
    }
}
