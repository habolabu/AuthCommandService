package edu.ou.authcommandservice.controller.accountSetting;

import edu.ou.authcommandservice.common.constant.EndPoint;
import edu.ou.authcommandservice.data.pojo.request.accountSetting.AccountSettingSwitchRequest;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(EndPoint.AccountSetting.BASE)
public class AccountSettingMultiSwitchController {
    private final IBaseService<IBaseRequest, IBaseResponse> accountSettingMultiSwitchService;

    /**
     * switch status of multi account setting
     *
     * @param accountSettingSwitchRequest account setting switch request
     * @return id of account setting
     * @author Nguyen Trung Kien - OU
     */
    @PreAuthorize(SecurityPermission.GRANT_PERMISSION)
    @PutMapping(
            value = EndPoint.AccountSetting.MULTIPLE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<IBaseResponse> switchMultiAccountSetting(
            @Validated
            @RequestBody
            AccountSettingSwitchRequest accountSettingSwitchRequest
    ) {
        return new ResponseEntity<>(
                accountSettingMultiSwitchService.execute(accountSettingSwitchRequest),
                HttpStatus.OK
        );
    }
}
