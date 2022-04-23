package hr.hsnopek.ecitizensintegration.domain.feature.authorization.mapper;

import authorizationresponsev3.PermissionType;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.model.SimplePermissionType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class PermissionTypeMapper {
    public static List<PermissionType> mapPermissionType(List<SimplePermissionType> permissionTypeList)
            throws DatatypeConfigurationException {
        List<authorizationresponsev3.PermissionType> permissionTypes = new ArrayList<>();
        for(SimplePermissionType simplePermissionType : permissionTypeList){
            PermissionType permissionType = new PermissionType();
            permissionType.setKey(simplePermissionType.getKey());
            permissionType.setValue(simplePermissionType.getValue());
            permissionType.setDescription(simplePermissionType.getDescription());
            permissionType.setValueDescription(simplePermissionType.getValueDescription());
            permissionType.setValidFrom(getGregorianCalendar(simplePermissionType.getValidFrom()));
            permissionType.setValidTo(getGregorianCalendar(simplePermissionType.getValidTo()));
            permissionTypes.add(permissionType);
        }
        return permissionTypes;
    }

    public static List<SimplePermissionType> mapSimplePermissionType(List<authorizationrequestv3.PermissionType> permissionTypeList) {
        List<SimplePermissionType> permissionTypes = new ArrayList<>();
        for(authorizationrequestv3.PermissionType permissionType : permissionTypeList){
            SimplePermissionType simplePermissionType = new SimplePermissionType();
            simplePermissionType.setKey(permissionType.getKey());
            simplePermissionType.setValue(permissionType.getValue());
            simplePermissionType.setDescription(permissionType.getDescription());
            simplePermissionType.setValueDescription(permissionType.getValueDescription());
            simplePermissionType.setValidFrom(getGregorianCalendar(permissionType.getValidFrom()));
            simplePermissionType.setValidTo(getGregorianCalendar(permissionType.getValidTo()));
            permissionTypes.add(simplePermissionType);
        }
        return permissionTypes;
    }

    private static XMLGregorianCalendar getGregorianCalendar(LocalDateTime localDateTime)
            throws DatatypeConfigurationException {
        if(localDateTime == null)
            return null;
        return DatatypeFactory
                .newInstance()
                .newXMLGregorianCalendar(GregorianCalendar.from(ZonedDateTime.of(localDateTime, ZoneId.systemDefault())));

    }
    private static LocalDateTime getGregorianCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
        if(xmlGregorianCalendar != null)
            return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
        else
            return null;
    }
}
