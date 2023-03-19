package edu.ou.authcommandservice.repository.role;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.entity.RoleEntity;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.repository.base.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoleDeActiveRepository  extends BaseRepository<RoleEntity, Integer> {
    private final BaseRepository<RoleEntity, Integer> roleUpdateRepository;
    private final ValidValidation validValidation;

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
                    "area"
            );
        }
    }

    /**
     * de-active exist role
     *
     * @param role role
     * @return id of role
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected Integer doExecute(RoleEntity role) {
        role.setIsDeleted(null);

        return roleUpdateRepository.execute(role);
    }

    @Override
    protected void postExecute(RoleEntity input) {
        // do nothing
    }
}
