package hr.hsnopek.ecitizensintegration.controller;

import hr.hsnopek.ecitizensintegration.domain.feature.person.service.PersonService;
import hr.hsnopek.ecitizensintegration.domain.feature.user.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final PersonService personService;

    public UserController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/get-user-data")
    public UserDTO getUserData(){
        return personService.getUserData();
    }

    @GetMapping("/get-user-session-data")
    public List<UserDTO> getUserSessionData(){
        return personService.getUserSessionData();
    }

    @GetMapping("/get-authorization-info")
    public Map<String, Object> getAuthorizationInfo() throws IOException {
        return personService.getAuthorizationInfo();
    }
}
