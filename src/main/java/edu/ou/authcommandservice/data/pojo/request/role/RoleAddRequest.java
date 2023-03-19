package edu.ou.authcommandservice.data.pojo.request.role;

import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RoleAddRequest implements IBaseRequest {
    @NotBlank
    @Size(
            min = 1,
            max = 255,
            message = "The length of string must be in range [1, 255]"
    )
    private String display;
}
