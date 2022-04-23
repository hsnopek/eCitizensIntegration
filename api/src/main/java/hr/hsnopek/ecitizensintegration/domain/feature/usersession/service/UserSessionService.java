package hr.hsnopek.ecitizensintegration.domain.feature.usersession.service;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.dtos.AuthnResponseTokenDto;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.entity.UserSession;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSessionService {

    @Autowired
    UserSessionRepository userSessionRepository;

    public UserSession saveUserSession(UserSession userSession){
        return userSessionRepository.save(userSession);
    }

    public UserSession createNewUserSession(AuthnResponseTokenDto token, String deviceId) {
        UserSession userSession = new UserSession();
        userSession.setDeviceId(deviceId);
        userSession.setSessionIndex(token.getSessionIndex());
        userSession.setNavToken(token.getNavToken());
        userSession.setSessionId(token.getSesijaId());
        userSession.setInResponseTo(token.getInResponseTo());
        return userSession;
    }
    public void updateUserSession(UserSession userSession, AuthnResponseTokenDto token, String deviceId) {
        userSession.setDeviceId(deviceId);
        userSession.setSessionIndex(token.getSessionIndex());
        userSession.setNavToken(token.getNavToken());
        userSession.setSessionId(token.getSesijaId());
        userSession.setInResponseTo(token.getInResponseTo());
        userSession.setUser(userSession.getUser());
    }

}
