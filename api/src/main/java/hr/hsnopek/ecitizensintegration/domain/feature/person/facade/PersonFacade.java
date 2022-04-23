package hr.hsnopek.ecitizensintegration.domain.feature.person.facade;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.dtos.AuthnResponseTokenDto;
import hr.hsnopek.ecitizensintegration.domain.feature.person.entity.Person;
import hr.hsnopek.ecitizensintegration.domain.feature.person.service.PersonService;
import hr.hsnopek.ecitizensintegration.domain.feature.role.entity.Role;
import hr.hsnopek.ecitizensintegration.domain.feature.role.enumeration.RoleNameEnum;
import hr.hsnopek.ecitizensintegration.domain.feature.role.repository.RoleRepository;
import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import hr.hsnopek.ecitizensintegration.domain.feature.user.service.UserService;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.entity.UserSession;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.service.UserSessionService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
public class PersonFacade {

    final PersonService personService;
    final UserService userService;
    final UserSessionService userSessionService;
    final RoleRepository roleRepository;

    public PersonFacade(PersonService personService, UserService userService, UserSessionService userSessionService, RoleRepository roleRepository){
        this.personService = personService;
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Person saveOrUpdatePerson(AuthnResponseTokenDto authnResponseToken, String deviceId){

        Optional<Person> personOptional = personService.findByTid(authnResponseToken.getTid());
        Person person = null;
        if (personOptional.isPresent()) {
            person = updatePerson(personOptional.get(), authnResponseToken, deviceId);
        } else {
            person = createNewPerson(authnResponseToken, deviceId);
        }

        return personService.savePerson(person);
    }

    private Person updatePerson(Person person, AuthnResponseTokenDto token, String deviceId) {
        person.setStateCode(token.getStateCode());
        person.setFirstName(token.getFirstName());
        person.setLastName(token.getLastName());
        person.setDateOfBirth(LocalDate.now());

        List<User> users = person.getUsers();

        if(!users.isEmpty()){

            List<UserSession> userSessionList = new ArrayList<>();
            for(User user : users){

                // Ako osoba ima drugačiji IDENT (OIB, DN), znači da se prijavila preko druge vjerodajnice
                // i mora mu joj se kreirati novi Korisnik

                if(!user.getIdent().equals(token.getIdent())){
                    User newUser = new User();
                    newUser.setIdent(token.getIdent());
                    newUser.setOib(token.getOib());
                    newUser.setDn(token.getDn());
                    newUser.setPerson(person);
                    newUser.getUserSessions().add(userSessionService.createNewUserSession(token, deviceId));
                    person.getUsers().add(newUser);
                } else {
                    // Ako ide preko iste vjerodajnice
                    for(UserSession userSession : user.getUserSessions()){
                        if(!userSession.getDeviceId().equals(deviceId)){
                            // ako ima novi uređaj, dodaj ga na listu
                            UserSession newUserSession = userSessionService.createNewUserSession(token,deviceId);
                            newUserSession.setUser(user);
                            userSessionList.add(newUserSession);
                        } else {
                            userSessionService.updateUserSession(userSession, token, deviceId);
                        }
                    }
                }
                user.getUserSessions().addAll(userSessionList);
            }
        }
        return person;
    }

    private Person createNewPerson(AuthnResponseTokenDto token, String deviceId) {
        Person person = new Person();
        person.setTid(token.getTid());
        person.setStateCode(token.getStateCode());
        person.setFirstName(token.getFirstName());
        person.setLastName(token.getLastName());
        person.setDateOfBirth(LocalDate.now());

        UserSession userSession = userSessionService.createNewUserSession(token, deviceId);

        User user = userService.createNewUser(token, person, userSession);
        mapDefaultRoleToUser(user);

        userSession.setUser(user);
        person.setUsers(List.of(user));
        return person;
    }

    private void mapDefaultRoleToUser(User newUser) {
        List<Role> roles = new ArrayList<>();
        Role userRole = roleRepository.findAll()
                .stream()
                .filter( role -> role.getRoleName().equals(RoleNameEnum.USER))
                .findFirst()
                .orElse(null);

        roles.add(userRole);
        newUser.setRoles(new HashSet<Role>(roles));
    }



}
