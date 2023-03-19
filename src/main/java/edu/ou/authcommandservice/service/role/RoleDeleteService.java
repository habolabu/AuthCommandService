package edu.ou.authcommandservice.service.role;

import edu.ou.authcommandservice.common.constant.CodeStatus;
import edu.ou.authcommandservice.data.pojo.request.role.RoleDeleteRequest;
import edu.ou.coreservice.common.constant.Message;
import edu.ou.coreservice.common.exception.BusinessException;
import edu.ou.coreservice.common.validate.ValidValidation;
import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import edu.ou.coreservice.data.pojo.response.base.IBaseResponse;
import edu.ou.coreservice.data.pojo.response.impl.SuccessPojo;
import edu.ou.coreservice.data.pojo.response.impl.SuccessResponse;
import edu.ou.coreservice.queue.auth.internal.role.RoleDeleteQueueI;
import edu.ou.coreservice.repository.base.IBaseRepository;
import edu.ou.coreservice.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleDeleteService extends BaseService<IBaseRequest, IBaseResponse> {
    private final IBaseRepository<Integer, Integer> roleDeleteRepository;
    private final IBaseRepository<Integer, Boolean> roleCheckExistByIdRepository;
    private final IBaseRepository<Integer, Boolean> roleHasAccountSettingsRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ValidValidation validValidation;

    /**
     * Validate input
     *
     * @param request request
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected void preExecute(IBaseRequest request) {
        if (validValidation.isInValid(request, RoleDeleteRequest.class)) {
            throw new BusinessException(
                    CodeStatus.INVALID_INPUT,
                    Message.Error.INVALID_INPUT,
                    "role"
            );
        }
    }

    /**
     * Delete exist role
     *
     * @param request request from client
     * @return response to client
     * @author Nguyen Trung Kien - OU
     */
    @Override
    protected IBaseResponse doExecute(IBaseRequest request) {
        final RoleDeleteRequest roleDeleteRequest = (RoleDeleteRequest) request;

        if (!roleCheckExistByIdRepository.execute(roleDeleteRequest.getId())) {
            throw new BusinessException(
                    CodeStatus.NOT_FOUND,
                    Message.Error.NOT_FOUND,
                    "role",
                    "role identity",
                    roleDeleteRequest.getId()
            );
        }

        if (roleHasAccountSettingsRepository.execute(roleDeleteRequest.getId())) {
            throw new BusinessException(
                    CodeStatus.NOT_EMPTY,
                    Message.Error.NOT_EMPTY,
                    "role"
            );
        }

        final int roleId = roleDeleteRepository.execute(roleDeleteRequest.getId());

        rabbitTemplate.convertSendAndReceive(
                RoleDeleteQueueI.EXCHANGE,
                RoleDeleteQueueI.ROUTING_KEY,
                roleId
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
