package hr.hsnopek.ecitizensintegration.domain.feature.nias.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.service.AuthorizationService;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.dtos.AuthnResponseTokenDto;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.mapper.AuthnResponseMapper;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.MetadataInfo;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.SamlTokenManager;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.AuthnRequestToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.AuthnResponseToken;
import hr.hsnopek.ecitizensintegration.domain.feature.person.entity.Person;
import hr.hsnopek.ecitizensintegration.domain.feature.person.facade.PersonFacade;
import hr.hsnopek.ecitizensintegration.domain.feature.person.service.PersonService;
import hr.hsnopek.ecitizensintegration.domain.feature.user.dto.UserDTO;
import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import hr.hsnopek.ecitizensintegration.domain.feature.user.mapper.UserDtoMapper;
import hr.hsnopek.ecitizensintegration.domain.feature.user.service.UserService;
import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.ConfigurationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.UnrecoverableKeyException;

@Service
public class NiasLoginService {

    private final SamlTokenManager samlTokenManager;
    private final MetadataInfo metadataInfo;
    private final PersonFacade personFacade;
    private final PersonService personService;
    private final UserService userService;
    private final JWTTokenUtil jwtTokenUtil;

    public NiasLoginService(SamlTokenManager samlTokenManager, MetadataInfo metadataInfo, PersonFacade personFacade,
                            PersonService personService, UserService userService,
                            JWTTokenUtil jwtTokenUtil, AuthorizationService authorizationService) {
        this.samlTokenManager = samlTokenManager;
        this.metadataInfo = metadataInfo;
        this.personFacade = personFacade;
        this.personService = personService;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public void signIn(HttpServletRequest req, HttpServletResponse resp) throws UnrecoverableKeyException, ConfigurationException, MessageEncodingException {
        resp.setHeader("User-Agent", getUserAgent(req));

        final HttpServletResponseAdapter responseAdapter = new HttpServletResponseAdapter(resp, true);
        AuthnRequestToken authnRequestToken = new AuthnRequestToken(metadataInfo);
        samlTokenManager.sendAuthnRequest(authnRequestToken, responseAdapter);
    }


    public void receiveAuthnResponse(HttpServletRequest req, HttpServletResponse res){
        try {
            AuthnResponseToken token = samlTokenManager.receiveAuthnResponse(new HttpServletRequestAdapter(req));
            if (!token.IsValid()) {
                res.sendRedirect(ApplicationProperties.FRONTEND_URL);
            }
            String userAgent = getUserAgent(req);
            AuthnResponseTokenDto authnResponseTokenDto = AuthnResponseMapper.map(token);
            Person person = personFacade.saveOrUpdatePerson(authnResponseTokenDto, userAgent);
            boolean isLocalUser = personService.isLocalUser(person);

            // this may be unnecessary call to the database, but it is easier to get userRoles this way than filtering person,
            // and it is called only once, during login
            User user = userService.findByIdentAndUserDevice(authnResponseTokenDto.getIdent(), userAgent);
            UserDTO userDTO = UserDtoMapper.map(authnResponseTokenDto, user, isLocalUser, userAgent);

            setCookiesInResponse(res, userDTO);
            redirectToFrontend(res, userDTO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCookiesInResponse(HttpServletResponse res, UserDTO userDTO) throws JsonProcessingException {
        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtTokenUtil.generateRefreshToken(userDTO.getTid()));
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) ApplicationProperties.REFRESH_TOKEN_EXPIRATION_TIME);

        // users that are in role of citizen can't have legalFunctions and permissions, so we don't map them here??

        String accessToken = jwtTokenUtil.generateAccessToken(
                userDTO.getTid(),
                userDTO,
                userDTO.getRoles(),
                null,
                null,
                true);

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60);

        res.addCookie(refreshTokenCookie);
        res.addCookie(accessTokenCookie);
    }

    private String getUserAgent(HttpServletRequest req) {
        return req.getHeader("User-Agent");
    }

    private void redirectToFrontend(HttpServletResponse resp, UserDTO userDTO) throws IOException {
        if(userDTO != null) {
            String redirectLinkLocalUser = String.format(
                    "%s/redirect-user/HR/HR/%s",
                    ApplicationProperties.FRONTEND_URL,
                    userDTO.getTid()
            );
            String redirectLinkForeignUser = String.format(
                    "%s/redirect-user/%s",
                    ApplicationProperties.FRONTEND_URL,
                    userDTO.getTid()
            );
            resp.sendRedirect(userDTO.getLocalUser() ? redirectLinkLocalUser : redirectLinkForeignUser);
        }
    }
}
