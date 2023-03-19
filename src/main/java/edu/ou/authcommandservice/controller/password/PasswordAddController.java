package edu.ou.authcommandservice.controller.password;

import edu.ou.authcommandservice.common.constant.EndPoint;
import edu.ou.authcommandservice.data.pojo.request.password.PasswordAddRequest;
import edu.ou.coreservice.common.constant.SecurityPermission;
import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import edu.ou.coreservice.data.pojo.response.base.IBaseResponse;
import edu.ou.coreservice.service.base.IBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(EndPoint.Password.BASE)
public class PasswordAddController {
    private final IBaseService<IBaseRequest, IBaseResponse> passwordAddService;

    /**
     * Add new password
     *
     * @param passwordAddRequest new password information
     * @return id of new password
     * @author Nguyen Trung Kien - OU
     */
    @PreAuthorize(SecurityPermission.AUTHENTICATED)
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<IBaseResponse> addNewPassword(
            @Validated
            @RequestBody
            PasswordAddRequest passwordAddRequest
    ) {
        return new ResponseEntity<>(
                passwordAddService.execute(passwordAddRequest),
                HttpStatus.OK
        );
    }
}
