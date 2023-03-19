package edu.ou.authcommandservice.data.pojo.request.role;

import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
public class RoleUpdateRequest implements IBaseRequest {
    @Min(
            value = 1,
            message = "The value must be greater than 1"
    )
    private int id;
    @Size(
            min = 1,
            max = 255,
            message = "The length of string must be in range [1, 255]"
    )
    private String display;
}
