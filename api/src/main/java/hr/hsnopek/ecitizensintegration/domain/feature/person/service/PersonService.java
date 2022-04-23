package hr.hsnopek.ecitizensintegration.domain.feature.person.service;

import hr.hsnopek.ecitizensintegration.domain.feature.person.entity.Person;
import hr.hsnopek.ecitizensintegration.domain.feature.person.repository.PersonRepository;
import hr.hsnopek.ecitizensintegration.domain.feature.user.dto.UserDTO;
import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import hr.hsnopek.ecitizensintegration.domain.feature.user.service.UserService;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.entity.UserSession;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.service.UserSessionService;
import hr.hsnopek.ecitizensintegration.general.util.SecurityUtils;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final UserService userService;
    private final UserSessionService userSessionService;
    private final JWTTokenUtil jwtTokenUtil;

    public PersonService(PersonRepository personRepository, UserService userService, UserSessionService userSessionService,
                         JWTTokenUtil jwtTokenUtil) {
        this.personRepository = personRepository;
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public Optional<Person> findByTid(String tid){
        return personRepository.findByTid(tid);
    }

    public List<UserDTO> getUserSessionData() {
        Optional<Person> optionalPerson = findByTid(SecurityUtils.getUsernameFromSpringSecurityContext());
        List<UserDTO> userDTOList = new ArrayList<>();
        if(optionalPerson.isEmpty()) {
            throw new RuntimeException("Can't get user data.");
        } else {
            Person person = optionalPerson.get();
            List<UserSession> userSessionList = new ArrayList<>();
            for(User user : person.getUsers()){
                userSessionList.addAll(user.getUserSessions());
            }
            userSessionList.forEach( userSession -> {
                UserDTO userDTO = new UserDTO();
                userDTO.setTid(person.getTid());
                userDTO.setFirstName(person.getFirstName());
                userDTO.setLastName(person.getLastName());
                userDTO.setDeviceId(userSession.getDeviceId());
                userDTO.setState(person.getStateCode());
                userDTO.setIdent(userSession.getUser().getIdent());
                userDTO.setSessionIndex(userSession.getSessionIndex());
                userDTO.setNavToken(userSession.getNavToken());
                userDTO.setSessionId(userSession.getSessionId());
                userDTOList.add(userDTO);
            });
        }
        return userDTOList;
    }

    public UserDTO getUserData() {
        return jwtTokenUtil.getUserDataFromJWT(SecurityUtils.getPrincipalFromSpringSecurityContext().getToken());
    }

    public Map<String, Object> getAuthorizationInfo() throws IOException {
        Map<String, Object> authorizationInfo = new HashMap<String, Object>();
        authorizationInfo.put("functions", jwtTokenUtil.getLegalFunctionsFromJWT(SecurityUtils.getPrincipalFromSpringSecurityContext().getToken()));
        authorizationInfo.put("permissions", jwtTokenUtil.getPermissionFromJWT(SecurityUtils.getPrincipalFromSpringSecurityContext().getToken()));
        return authorizationInfo;
    }

    public Person savePerson(Person person){
        return personRepository.save(person);
    }

    public boolean isLocalUser(Person person){
        return StringUtils.isNotBlank(person.getStateCode()) && person.getStateCode().equals("HR");
    }

}
