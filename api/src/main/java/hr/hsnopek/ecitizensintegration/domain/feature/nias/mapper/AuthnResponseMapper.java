package hr.hsnopek.ecitizensintegration.domain.feature.nias.mapper;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.AuthnResponseToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.dtos.AuthnResponseTokenDto;
import org.apache.commons.lang.StringUtils;

public class AuthnResponseMapper {

    public static AuthnResponseTokenDto map(AuthnResponseToken token){

        AuthnResponseTokenDto authnResponseTokenDto = new AuthnResponseTokenDto();
        authnResponseTokenDto.setStateCode(!StringUtils.isBlank(token.getOznakaDrzaveEid()) ?
                "HR" : token.getPersonIdentifier().split("/")[0]);

        if(authnResponseTokenDto.getStateCode().equals("HR")) {
            authnResponseTokenDto.setIdent(!StringUtils.isBlank(token.getDN()) ? token.getDN() : token.getOIB());
            authnResponseTokenDto.setDn(token.getDN());
            authnResponseTokenDto.setFirstName(token.getIme());
            authnResponseTokenDto.setLastName(token.getPrezime());
        } else {
            authnResponseTokenDto.setIdent(token.getPersonIdentifier());
            authnResponseTokenDto.setFirstName(token.getCurrentGivenName());
            authnResponseTokenDto.setLastName(token.getCurrentFamilyName());
            authnResponseTokenDto.setBirthName(token.getBirthName());
            authnResponseTokenDto.setDateOfBirth(token.getDateOfBirth());
            authnResponseTokenDto.setPersonIdentifier(token.getPersonIdentifier());
            authnResponseTokenDto.setPlaceOfBirth(token.getPlaceOfBirth());
            authnResponseTokenDto.setCurrentAddress(token.getCurrentAddress());
            authnResponseTokenDto.setGender(token.getGender());
        }
        authnResponseTokenDto.setTid(!StringUtils.isBlank(token.getTID()) ? token.getTID() : token.getPersonIdentifier());
        authnResponseTokenDto.setOib(token.getOIB() != null ? token.getOIB() : authnResponseTokenDto.getIdent());
        authnResponseTokenDto.setNavToken(token.getNavToken());
        authnResponseTokenDto.setSessionIndex(token.getSessionIndex());
        authnResponseTokenDto.setSesijaId(token.getSesijaId());
        authnResponseTokenDto.setInResponseTo(token.getInResponseTo());

        return authnResponseTokenDto;
    }
}
