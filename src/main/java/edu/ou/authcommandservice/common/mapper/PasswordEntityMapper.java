package edu.ou.authcommandservice.common.mapper;

import edu.ou.authcommandservice.data.entity.PasswordEntity;
import edu.ou.authcommandservice.data.pojo.request.password.PasswordAddRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PasswordEntityMapper {
    PasswordEntityMapper INSTANCE = Mappers.getMapper(PasswordEntityMapper.class);

    /**
     * Map PasswordAddRequest object to PasswordEntity object
     *
     * @param passwordAddRequest represents for PasswordAddRequest object
     * @return PasswordEntity object
     * @author Nguyen Trung Kien - OU
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PasswordEntity fromPasswordAddRequest(PasswordAddRequest passwordAddRequest);

}
