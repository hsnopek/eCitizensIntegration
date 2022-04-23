package hr.hsnopek.ecitizensintegration.controller;

import hr.hsnopek.ecitizensintegration.domain.feature.navigationbar.service.NavigationBarService;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.service.NiasLoginService;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.service.NiasLogoutService;
import hr.hsnopek.ecitizensintegration.domain.feature.person.repository.PersonRepository;
import hr.hsnopek.ecitizensintegration.domain.feature.person.service.PersonService;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.ConfigurationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/nias")
public class NiasController {

    private final NiasLoginService niasLoginService;
    private final NiasLogoutService niasLogoutService;
    private final NavigationBarService navigationBarService;
    private final PersonService personService;
    private final JWTTokenUtil jwtTokenUtil;

    public NiasController(NiasLoginService niasLoginService, NiasLogoutService niasLogoutService, PersonRepository personRepository, NavigationBarService navigationBarService, PersonService personService, JWTTokenUtil jwtTokenUtil) {
        this.niasLoginService = niasLoginService;
        this.niasLogoutService = niasLogoutService;
        this.navigationBarService = navigationBarService;
        this.personService = personService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping("/sign-in")
    public void signIn(HttpServletRequest req, HttpServletResponse resp) throws UnrecoverableKeyException, ConfigurationException, MessageEncodingException {
        niasLoginService.signIn(req, resp);
    }

    @PostMapping("/receive-authn-response")
    public void receiveAuthnResponse(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        niasLoginService.receiveAuthnResponse(req, resp);
    }

    @GetMapping("/log-out")
    public void logOut(
            @RequestParam("sessionIndex") String sessionIndex,
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        niasLogoutService.logout(sessionIndex, req, resp);
    }

    @PostMapping("/receive-logout-response")
    private void receiveLogoutResponse(HttpServletRequest req, HttpServletResponse res) {
        niasLogoutService.receiveLogoutResponse(req, res);
    }

    @PostMapping("/single-sign-out")
    private void singleSignOutService(HttpServletRequest req, HttpServletResponse resp) {
        niasLogoutService.recieveLogoutRequestAndSendLogoutResponse(req, resp);
    }

    @GetMapping("/navigation-bar-data")
    public ResponseEntity<?> getNavigationBarData(){

        Map<String, String> navigationBarData = new HashMap<>();
        try {
            navigationBarData = navigationBarService.getNavigationBarData();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(navigationBarData);
        }
        return ResponseEntity.status(HttpStatus.OK).body(navigationBarData);
    }
}
