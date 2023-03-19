package edu.ou.authcommandservice.controller.role;

import edu.ou.authcommandservice.common.constant.EndPoint;
import edu.ou.authcommandservice.data.pojo.request.role.RoleUpdateRequest;
import edu.ou.coreservice.common.constant.SecurityPermission;
import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import edu.ou.coreservice.data.pojo.response.base.IBaseResponse;
import edu.ou.coreservice.service.base.IBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(EndPoint.Role.BASE)
public class RoleUpdateController {
    private final IBaseService<IBaseRequest, IBaseResponse> roleUpdateService;

    /**
     * Update exist password
     *
     * @param roleUpdateRequest new role information
     * @return id of new role
     * @author Nguyen Trung Kien - OU
     */
    @PreAuthorize(SecurityPermission.MODIFY_EXIST_ROLE)
    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<IBaseResponse> updateExistRole(
            @Validated
            @RequestBody
            RoleUpdateRequest roleUpdateRequest
    ) {
        return new ResponseEntity<>(
                roleUpdateService.execute(roleUpdateRequest),
                HttpStatus.OK
        );
    }
}
