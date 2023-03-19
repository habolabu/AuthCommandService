package edu.ou.authcommandservice.common.mapper;

import edu.ou.authcommandservice.data.entity.RoleEntity;
import edu.ou.authcommandservice.data.pojo.request.role.RoleAddRequest;
import edu.ou.authcommandservice.data.pojo.request.role.RoleUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleEntityMapper {
    RoleEntityMapper INSTANCE = Mappers.getMapper(RoleEntityMapper.class);

    /**
     * Map RoleAddRequest object to RoleEntity object
     *
     * @param roleAddRequest represents for RoleAddRequest object
     * @return RoleEntity object
     * @author Nguyen Trung Kien - OU
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "display", qualifiedByName = "displayToName")
    @Mapping(target = "display", source = "display")
    @Mapping(target = "isDeleted", ignore = true)
    RoleEntity fromRoleAddRequest(RoleAddRequest roleAddRequest);

    /**
     * Map RoleUpdateRequest object to RoleEntity object
     *
     * @param roleUpdateRequest represents for RoleUpdateRequest object
     * @return RoleEntity object
     * @author Nguyen Trung Kien - OU
     */
    @Mapping(target = "name", source = "display", qualifiedByName = "displayToName")
    @Mapping(target = "isDeleted", ignore = true)
    RoleEntity fromRoleUpdateRequest(RoleUpdateRequest roleUpdateRequest);

    /**
     * Convert display to name
     *
     * @param display display of role
     * @return name of role
     * @author Nguyen Trung Kien - OU
     */
    @Named("displayToName")
    static String toName(String display) {
        return display.replace(" ", "_");
    }
}
