package hr.hsnopek.ecitizensintegration.domain.feature.nias.service;

import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.MetadataInfo;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.SamlTokenManager;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.LogoutRequestToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.LogoutResponseToken;
import hr.hsnopek.ecitizensintegration.domain.feature.refreshtoken.util.RefreshTokenUtil;
import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import hr.hsnopek.ecitizensintegration.domain.feature.user.service.UserService;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.service.UserSessionService;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil;
import org.opensaml.Configuration;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.SecurityConfiguration;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class NiasLogoutService {

    private final SamlTokenManager samlTokenManager;
    private final MetadataInfo metadataInfo;
    private final UserService userService;
    private final UserSessionService userSessionService;
    private final JWTTokenUtil jwtTokenUtil;

    public NiasLogoutService(SamlTokenManager samlTokenManager, MetadataInfo metadataInfo, UserService userService,
                             UserSessionService userSessionService, JWTTokenUtil jwtTokenUtil) {
        this.samlTokenManager = samlTokenManager;
        this.metadataInfo = metadataInfo;
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    public void logout(String sessionIndex, HttpServletRequest req, HttpServletResponse res) throws IOException {
        // if foreign user detected, clean up refresh token cookie and redirect to eCitizens portal
        if(sessionIndex.startsWith("fgn")){
            RefreshTokenUtil.setRefreshTokenInCookie(res, null);
            res.sendRedirect(ApplicationProperties.FRONTEND_URL + "/logout?redirectUrl=" + ApplicationProperties.NIAS_PORTAL_URL);
        }

        Cookie refreshTokenCookie = WebUtils.getCookie(req, "refreshToken");
        if(refreshTokenCookie != null) {
            String jwt = refreshTokenCookie.getValue();
            String tid = jwtTokenUtil.getUsernameFromJWT(jwt);
            try {
                LogoutRequestToken logoutRequest = new LogoutRequestToken(metadataInfo, tid, sessionIndex);
                signLogoutRequest(logoutRequest);

                samlTokenManager.sendLogoutRequest(
                        logoutRequest,
                        SAMLConstants.SAML2_POST_BINDING_URI,
                        new HttpServletResponseAdapter(res, true)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveLogoutResponse(HttpServletRequest req, HttpServletResponse res){
        try {
            LogoutResponseToken token = samlTokenManager.receiveLogoutResponse(new HttpServletRequestAdapter(req));
            if (!token.IsValid()) {
                return;
            }
            String status = token.logoutResponse.getStatus().getStatusCode().getValue();

            if (status.equalsIgnoreCase(StatusCode.SUCCESS_URI)) {
                res.sendRedirect(ApplicationProperties.FRONTEND_URL + "/logout?redirectUrl=" + ApplicationProperties.NIAS_PORTAL_URL);
            } else {
                res.sendRedirect(ApplicationProperties.FRONTEND_URL + "/logout?redirectUrl=" + ApplicationProperties.FRONTEND_URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void receiveLogoutRequestAndSendLogoutResponse(HttpServletRequest req, HttpServletResponse resp){
        try {
            LogoutRequestToken logoutRequest = samlTokenManager.receiveLogoutRequest(new HttpServletRequestAdapter(req));
            if (!logoutRequest.IsValid()) {
                return;
            }
            String subjectId = logoutRequest.logoutRequest.getNameID().getValue();
            String sessionIndex = logoutRequest.logoutRequest.getSessionIndexes().get(0).getSessionIndex();

            User user = userService.findByIdent(subjectId);

            user.getUserSessions()
                    .stream()
                    .filter( session -> session.getSessionIndex().equalsIgnoreCase(sessionIndex))
                    .forEach( item -> {
                        item.setSessionIndex(null);
                        userSessionService.saveUserSession(item);
                    });
            LogoutResponseToken logoutResponse = new LogoutResponseToken(
                    metadataInfo,
                    metadataInfo.getSSOut(metadataInfo.niasEntityDescriptor, SAMLConstants.SAML2_SOAP11_BINDING_URI).getLocation(),
                    logoutRequest.logoutRequest.getID(),
                    StatusCode.SUCCESS_URI);

            samlTokenManager.sendLogoutResponse(logoutResponse, new HttpServletResponseAdapter(resp, false));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Signature getSignature(){
        BasicX509Credential signingCredential = new BasicX509Credential();
        signingCredential.setEntityCertificate(metadataInfo.serviceCertificate);
        signingCredential.setPrivateKey(metadataInfo.serviceCredentials.getPrivateKey());

        Signature signature = new SignatureBuilder().buildObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSigningCredential(signingCredential);
        signature.setSignatureAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");

        SecurityConfiguration securityConfiguration = Configuration.getGlobalSecurityConfiguration();

        try{
            SecurityHelper.prepareSignatureParams(signature, signingCredential, securityConfiguration, null);
        } catch (SecurityException se){
            throw new RuntimeException("Dogodila se greška prilikom potpisa. " + se.getMessage());
        }
        return signature;
    }

    private void signLogoutRequest(LogoutRequestToken logoutRequestToken){
        Signature signature = getSignature();
        logoutRequestToken.getRequest().setSignature(signature);
        try{
            Configuration.getMarshallerFactory().getMarshaller(logoutRequestToken.getRequest()).marshall(logoutRequestToken.getRequest());
        } catch (MarshallingException me){
            throw new RuntimeException("Pogreška prilikom marshalliranja. " + me.getMessage());
        }

        try{
            Signer.signObject(signature);
        } catch(SignatureException se){
            throw new RuntimeException("Dogodila se greška prilikom potpisa. " + se.getMessage());
        }
    }


    private void signLogoutResponse(LogoutResponseToken logoutResponseToken){
        Signature signature = getSignature();
        logoutResponseToken.getResponse().setSignature(signature);
        try{
            Configuration.getMarshallerFactory().getMarshaller(logoutResponseToken.getResponse()).marshall(logoutResponseToken.getResponse());
        } catch (MarshallingException me){
            throw new RuntimeException("Pogreška prilikom marshalliranja. " + me.getMessage());
        }

        try{
            Signer.signObject(signature);
        } catch(SignatureException se){
            throw new RuntimeException("Dogodila se greška prilikom potpisa. " + se.getMessage());
        }
    }

    private String getUserAgent(HttpServletRequest req) {
        return req.getHeader("User-Agent");
    }

}
