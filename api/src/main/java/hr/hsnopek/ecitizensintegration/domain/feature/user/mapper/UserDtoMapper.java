package hr.hsnopek.ecitizensintegration.domain.feature.user.mapper;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.dtos.AuthnResponseTokenDto;
import hr.hsnopek.ecitizensintegration.domain.feature.user.dto.UserDTO;
import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;

import java.util.stream.Collectors;

public class UserDtoMapper {

    public UserDtoMapper(){}

    public static UserDTO map(AuthnResponseTokenDto authnResponseTokenDto, User user, Boolean isLocalUser, String deviceId){
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(authnResponseTokenDto.getFirstName());
        userDTO.setLastName(authnResponseTokenDto.getLastName());
        userDTO.setState(authnResponseTokenDto.getStateCode());
        userDTO.setIdent(authnResponseTokenDto.getIdent());
        userDTO.setTid(authnResponseTokenDto.getTid());
        userDTO.setNavToken(authnResponseTokenDto.getNavToken());
        userDTO.setSessionIndex(authnResponseTokenDto.getSessionIndex());
        userDTO.setSessionId(authnResponseTokenDto.getSesijaId());
        userDTO.setLocalUser(isLocalUser);
        userDTO.setInResponseTo(authnResponseTokenDto.getInResponseTo());

        userDTO.setDeviceId(deviceId);

        userDTO.setRoles(user.getRoles()
                .stream()
                .map(role -> role.getRoleName().toString())
                .collect(Collectors.joining(",")));
        return userDTO;
    }

    public static UserDTO map(User user, String userRoles, String deviceId){
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(user.getPerson().getFirstName());
        userDTO.setLastName(user.getPerson().getLastName());
        userDTO.setState(user.getPerson().getStateCode());
        userDTO.setIdent(user.getIdent());
        userDTO.setTid(user.getPerson().getTid());
        userDTO.setDeviceId(deviceId);
        userDTO.setRoles(userRoles);
        return userDTO;
    }
}
