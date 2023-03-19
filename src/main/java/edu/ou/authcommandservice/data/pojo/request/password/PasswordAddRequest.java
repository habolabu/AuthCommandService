package edu.ou.authcommandservice.data.pojo.request.password;

import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PasswordAddRequest implements IBaseRequest {
    @NotBlank
    @Size(
            min = 8,
            max = 10,
            message = "Length must be in range 8 - 10"
    )
    private String rawPassword;
}
