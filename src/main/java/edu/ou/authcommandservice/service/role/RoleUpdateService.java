package edu.ou.authcommandservice.service.role;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.common.mapper.RoleEntityMapper;
import edu.ou.authcommandservice.data.entity.RoleEntity;
import edu.ou.authcommandservice.data.pojo.request.role.RoleUpdateRequest;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import edu.ou.coreservice.data.pojo.response.base.IBaseResponse;
import edu.ou.coreservice.data.pojo.response.impl.SuccessPojo;
import edu.ou.coreservice.data.pojo.response.impl.SuccessResponse;
import edu.ou.coreservice.queue.auth.internal.role.RoleUpdateQueueI;
import edu.ou.coreservice.repository.base.IBaseRepository;
import edu.ou.coreservice.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleUpdateService extends BaseService<IBaseRequest, IBaseResponse> {
    private final IBaseRepository<RoleEntity, Integer> roleUpdateRepository;
    private final IBaseRepository<Integer, Boolean> roleCheckExistByIdRepository;
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
        if (validValidation.isInValid(request, RoleUpdateRequest.class)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "role"
            );
        }
    }

    /**
     * Update exist role
     *
     * @param request request from client
     * @return response to client
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected IBaseResponse doExecute(IBaseRequest request) {
        final RoleEntity roleEntity = RoleEntityMapper.INSTANCE
                .fromRoleUpdateRequest((RoleUpdateRequest) request);

        if (!roleCheckExistByIdRepository.execute(roleEntity.getId())) {
            throw new BusinessException(
                    CodeStatus.NOT_FOUND,
                    Message.Error.NOT_FOUND,
                    "role",
                    "role identity",
                    roleEntity.getId()
            );
        }

        final int roleId = roleUpdateRepository.execute(roleEntity);

        rabbitTemplate.convertSendAndReceive(
                RoleUpdateQueueI.EXCHANGE,
                RoleUpdateQueueI.ROUTING_KEY,
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
