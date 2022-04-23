package hr.hsnopek.ecitizensintegration.domain.feature.role.converters;

import hr.hsnopek.ecitizensintegration.domain.feature.role.enumeration.RoleNameEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RoleNameConverter implements AttributeConverter<RoleNameEnum, String> {

    @Override
    public String convertToDatabaseColumn(RoleNameEnum roleNameEnum) {
        return roleNameEnum.toString();
    }

    @Override
    public RoleNameEnum convertToEntityAttribute(String roleName) {
        return RoleNameEnum.fromString(roleName);
    }

}
