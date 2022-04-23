package hr.hsnopek.ecitizensintegration.domain.feature.authorization.facade;

import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.service.AuthorizationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

@Service
public class AuthorizationFacade {

    private final AuthorizationService authorizationService;

    public AuthorizationFacade(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void actAsEntity(String forPersonOib, String forLegalIps, Integer forLegalIzvor, String toLegalIps,
                            Integer toLegalIzvor, String ident, HttpServletRequest httpServletRequest,
                            HttpServletResponse httpServletResponse) throws UnrecoverableKeyException, JAXBException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        String accessToken = authorizationService.actAsEntityAndGenerateNewAccessToken(
                ident,forLegalIps,forLegalIzvor,toLegalIps,toLegalIzvor,httpServletRequest);

        String redirectUrl = getRedirectUrl(forPersonOib, forLegalIps, forLegalIzvor, toLegalIps, toLegalIzvor);
        redirectToFrontend(accessToken, redirectUrl, httpServletResponse);

    }


    private String getRedirectUrl(String forPersonOib, String forLegalIps, Integer forLegalIzvor, String toLegalIps,
                                  Integer toLegalIzvor){

        String tForPersonOib = !StringUtils.isBlank(forPersonOib) ? forPersonOib : StringUtils.EMPTY;
        String tForLegalIps = !StringUtils.isBlank(forLegalIps) ? forLegalIps : StringUtils.EMPTY;
        String tForLegalIzvor = forLegalIzvor != null ? String.valueOf(forLegalIzvor) : StringUtils.EMPTY;
        String tToLegalIps =!StringUtils.isBlank(toLegalIps) ? toLegalIps : StringUtils.EMPTY;
        String tToLegalIzvor = toLegalIzvor != null ? String.valueOf(toLegalIzvor) : StringUtils.EMPTY;

        return String.format(
                "%s%s?ForPersonOib=%s&ForLegalIps=%s&ForLegalIzvor_reg=%s&ToLegalIps=%s&ToLegalIzvor_reg=%s",
                ApplicationProperties.FRONTEND_URL,
                ApplicationProperties.AUTHORIZATION_SERVICE_FRONTEND_REDIRECT_URL,
                tForPersonOib,
                tForLegalIps,
                tForLegalIzvor,
                tToLegalIps,
                tToLegalIzvor);
    }


    private void redirectToFrontend(String accessToken, String redirectUrl, HttpServletResponse httpServletResponse) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60);
        httpServletResponse.addCookie(accessTokenCookie);

        // redirect to frontend and set new accessToken with chosen subjects
        httpServletResponse.setHeader("Location", redirectUrl);
        httpServletResponse.setStatus(302);
    }
}
