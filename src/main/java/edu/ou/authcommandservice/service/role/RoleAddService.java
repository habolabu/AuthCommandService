package edu.ou.authcommandservice.service.role;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.common.mapper.RoleEntityMapper;
import edu.ou.authcommandservice.data.entity.RoleEntity;
import edu.ou.authcommandservice.data.pojo.request.role.RoleAddRequest;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import edu.ou.coreservice.data.pojo.response.base.IBaseResponse;
import edu.ou.coreservice.data.pojo.response.impl.SuccessPojo;
import edu.ou.coreservice.data.pojo.response.impl.SuccessResponse;
import edu.ou.coreservice.queue.auth.internal.role.RoleAddQueueI;
import edu.ou.coreservice.repository.base.IBaseRepository;
import edu.ou.coreservice.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleAddService extends BaseService<IBaseRequest, IBaseResponse> {
    private final IBaseRepository<RoleEntity, Integer> roleAddRepository;
    private final IBaseRepository<String, RoleEntity> roleFindByNameWithDeletedRepository;
    private final IBaseRepository<String, Boolean> roleCheckDeleteRepository;
    private final IBaseRepository<RoleEntity, Integer> roleDeActiveRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ValidValidation validValidation;

    /**
     * Validate request
     *
     * @param request input of task
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(IBaseRequest request) {
        if (validValidation.isInValid(request, RoleAddRequest.class)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "role"
            );
        }
    }

    /**
     * Insert new role
     *
     * @param request request from client
     * @return response to client
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected IBaseResponse doExecute(IBaseRequest request) {
        final RoleEntity roleEntity = RoleEntityMapper.INSTANCE.fromRoleAddRequest((RoleAddRequest) request);

        int roleId;

        if (roleCheckDeleteRepository.execute(roleEntity.getName())) {
            final RoleEntity existDeletedRoleEntity =
                    roleFindByNameWithDeletedRepository.execute(roleEntity.getName());
            roleEntity.setId(existDeletedRoleEntity.getId());
            roleId = roleDeActiveRepository.execute(roleEntity);

        } else {
            roleId = roleAddRepository.execute(roleEntity);
        }

        roleEntity.setId(roleId);

        rabbitTemplate.convertSendAndReceive(
                RoleAddQueueI.EXCHANGE,
                RoleAddQueueI.ROUTING_KEY,
                roleEntity
        );

        return new SuccessResponse<>(
                new SuccessPojo<>(
                        roleId,
                        CodeStatus.SUCCESS,
                        Message.Success.SUCCESSFUL
                )
        );
    }

    @Override
    protected void postExecute(IBaseRequest input) {
        // do nothing
    }
}
