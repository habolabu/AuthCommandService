package edu.ou.authcommandservice.data.pojo.request.role;

import edu.ou.coreservice.data.pojo.request.base.IBaseRequest;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class RoleDeleteRequest implements IBaseRequest {
    @Min(
            value = 1,
            message = "The value must be greater than 1"
    )
    private int id;
}
