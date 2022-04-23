package hr.hsnopek.ecitizensintegration.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("applicationProperties")
public class ApplicationProperties {

    // APP
    public static String APPLICATION_URL;
    public static String FRONTEND_URL;

    public static Boolean EMAIL_VERIFICATION_ENABLED;
    public static String JWT_SECRET;
    public static long REFRESH_TOKEN_EXPIRATION_TIME;
    public static long ACCESS_TOKEN_EXPIRATION_TIME;

    public static String APPLICATION_CERTIFICATE_PASSWORD;
    public static String APPLICATION_CERTIFICATE_ALIAS;
    public static String APPLICATION_KEYSTORE_PATH;
    public static String APPLICATION_KEYSTORE_PASSWORD;
    public static String APPLICATION_TRUSTSTORE_PATH;
    public static String APPLICATION_TRUSTSTORE_PASSWORD;

    // NIAS
    public static String NIAS_PORTAL_URL;
    public static String NIAS_METADATA_URL;
    public static String NIAS_LOGIN_RESPONSE_URL;
    public static String NIAS_PUBLIC_KEY_ALIAS;

    // NAVIGATION BAR

    public static String NAVIGATION_BAR_SHOW_ENTITY_SEARCH;
    public static String NAVIGATION_BAR_SHOW_PERSONS;
    public static String NAVIGATION_BAR_SHOW_ENTITIES;
    public static String NAVIGATION_BAR_SHOW_VISION_IMPAIRED;
    public static String NAVIGATION_BAR_SHOW_DYSLEXIA;
    public static String NAVIGATION_BAR_SHOW_FONT_RESIZE;
    public static String NAVIGATION_BAR_SHOW_LOGIN_BUTTON;

    // AUTHORIZATION SERVICE
    public static String AUTHORIZATION_SERVICE_PUBLIC_KEY_ALIAS;
    public static String AUTHORIZATION_SERVICE_AUTHORIZATION_DATA_URL;
    public static String AUTHORIZATION_SERVICE_FRONTEND_REDIRECT_URL;

    // OKP
    public static String OKP_SERVICE_URL;
    public static String OKP_SENDER_ID;
    public static String OKP_SERVICE_ID;
    public static String OKP_PROVJERA_SERVICE_ID;
    public static String OKP_ID_POSILJATELJA;
    public static String OKP_TIP_PORUKE;
    public static String OKP_SIGURNOSNA_RAZINA_PORUKE;


    @Value("${application.url}")
    public void setApplicationUrl(String applicationUrl) {
        ApplicationProperties.APPLICATION_URL = applicationUrl;
    }
    @Value("${application.frontend-url}")
    public void setFrontendUrl(String frontendUrl) {
        ApplicationProperties.FRONTEND_URL = frontendUrl;
    }
    @Value("${application.jwt.secret}")
    public void setJwtSecret(String jwtSecret) {
        ApplicationProperties.JWT_SECRET = jwtSecret;
    }
    @Value("${application.jwt.refreshtoken.expiration}")
    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        ApplicationProperties.REFRESH_TOKEN_EXPIRATION_TIME = refreshTokenExpiration;
    }
    @Value("${application.jwt.accesstoken.expiration}")
    public void setAccesTokenExpiration(long accesTokenExpiration) {
        ApplicationProperties.ACCESS_TOKEN_EXPIRATION_TIME = accesTokenExpiration;
    }
    @Value("${application.certificate.alias}")
    public void setApplicationCertificateAlias(String applicationCertificateAlias){
        ApplicationProperties.APPLICATION_CERTIFICATE_ALIAS = applicationCertificateAlias;
    }
    @Value("${application.certificate.password}")
    public void setApplicationCertificatePassword(String applicationCertificatePassword){
        ApplicationProperties.APPLICATION_CERTIFICATE_PASSWORD= applicationCertificatePassword;
    }
    @Value("${application.keystore.path}")
    public void setApplicationKeystorePath(String applicationKeystorePath){
        ApplicationProperties.APPLICATION_KEYSTORE_PATH = applicationKeystorePath;
    }
    @Value("${application.keystore.password}")
    public void setApplicationKeystorePassword(String applicationKeystorePassword){
        ApplicationProperties.APPLICATION_KEYSTORE_PASSWORD = applicationKeystorePassword;
    }
    @Value("${application.truststore.path}")
    public void setApplicationTruststorePath(String applicationTruststorePath){
        ApplicationProperties.APPLICATION_TRUSTSTORE_PATH = applicationTruststorePath;
    }
    @Value("${application.truststore.password}")
    public void setApplicationTruststorePassword(String applicationTruststorePassword){
        ApplicationProperties.APPLICATION_TRUSTSTORE_PASSWORD = applicationTruststorePassword;
    }

    @Value("${nias.portal.url}")
    public void setNiasPortalUrl(String niasPortalUrl){
        ApplicationProperties.NIAS_PORTAL_URL = niasPortalUrl;
    }

    @Value("${nias.metadata.url}")
    public void setNiasMetadataUrl(String niasMetadataUrl){
        ApplicationProperties.NIAS_METADATA_URL = niasMetadataUrl;
    }

    @Value("${nias.login-response.url}")
    public void setNiasLoginResponseUrl(String niasLoginResponseUrl){
        ApplicationProperties.NIAS_LOGIN_RESPONSE_URL = niasLoginResponseUrl;
    }

    @Value("${nias.publickey.alias}")
    public void setNiasPublicKeyAlias(String niasPublicKeyAlias){
        ApplicationProperties.NIAS_PUBLIC_KEY_ALIAS = niasPublicKeyAlias;
    }

    @Value("${authorizationservice.publickey.alias}")
    public void setAuthorizationServicePublicKeyAlias(String authorizationServicePublicKeyAlias){
        ApplicationProperties.AUTHORIZATION_SERVICE_PUBLIC_KEY_ALIAS = authorizationServicePublicKeyAlias;
    }

    @Value("${authorizationservice.authorization-data.url}")
    public void setAuthorizationServiceAuthorizationDataUrl(String authorizationDataUrl){
        ApplicationProperties.AUTHORIZATION_SERVICE_AUTHORIZATION_DATA_URL = authorizationDataUrl;
    }
    @Value("${authorizationservice.frontend-redirect-url}")
    public void setAuthorizationServiceFrontendRedirectUrl(String frontendRedirectUrl){
        ApplicationProperties.AUTHORIZATION_SERVICE_FRONTEND_REDIRECT_URL = frontendRedirectUrl;
    }

    @Value("${okp.service-url}")
    public void setOkpServiceUrl(String serviceUrl){
        ApplicationProperties.OKP_SERVICE_URL = serviceUrl;
    }

    @Value("${okp.sender-id}")
    public void setOkpSenderId(String senderId){
        ApplicationProperties.OKP_SENDER_ID = senderId;
    }

    @Value("${okp.service-id}")
    public void setOkpServiceId(String serviceId){
        ApplicationProperties.OKP_SERVICE_ID = serviceId;
    }
    @Value("${okp.provjera.service-id}")
    public void setOkpProvjeraServiceId(String provjeraServiceId){
        ApplicationProperties.OKP_PROVJERA_SERVICE_ID = provjeraServiceId;
    }

    @Value("${okp.id-posiljatelja}")
    public void setOkpIdPosiljatelja(String idPosiljatelja){
        ApplicationProperties.OKP_ID_POSILJATELJA = idPosiljatelja;
    }

    @Value("${okp.tip-poruke}")
    public void setOkpTipPoruke(String tipPoruke){
        ApplicationProperties.OKP_TIP_PORUKE = tipPoruke;
    }

    @Value("${okp.sigurnosna-razina-poruke}")
    public void setOkpSigurnosnaRazinaPoruke(String sigurnosnaRazinaPoruke){
        ApplicationProperties.OKP_SIGURNOSNA_RAZINA_PORUKE = sigurnosnaRazinaPoruke;
    }

    @Value("${navigationbar.show-entity-search}")
    public void setNavigationBarShowEntitySearch(String navigationBarShowEntitySearch){
        ApplicationProperties.NAVIGATION_BAR_SHOW_ENTITY_SEARCH = navigationBarShowEntitySearch;
    }
    @Value("${navigationbar.show-persons}")
    public void setNavigationBarShowPersons(String navigationBarShowPersons){
        ApplicationProperties.NAVIGATION_BAR_SHOW_PERSONS = navigationBarShowPersons;
    }
    @Value("${navigationbar.show-entities}")
    public void setNavigationBarShowEntities(String navigationBarShowEntities){
        ApplicationProperties.NAVIGATION_BAR_SHOW_ENTITIES = navigationBarShowEntities;
    }
    @Value("${navigationbar.show-entities}")
    public void setNavigationBarShowVisionImpaired(String navigationBarShowVisionImpaired){
        ApplicationProperties.NAVIGATION_BAR_SHOW_VISION_IMPAIRED = navigationBarShowVisionImpaired;
    }
    @Value("${navigationbar.show-dyslexia}")
    public void setNavigationBarShowDyslexia(String navigationBarShowDyslexia){
        ApplicationProperties.NAVIGATION_BAR_SHOW_DYSLEXIA = navigationBarShowDyslexia;
    }
    @Value("${navigationbar.show-font-resize}")
    public void setNavigationBarShowFontResize(String navigationBarShowFontResize){
        ApplicationProperties.NAVIGATION_BAR_SHOW_FONT_RESIZE = navigationBarShowFontResize;
    }
    @Value("${navigationbar.show-login-button}")
    public void setNavigationBarShowLoginButton(String navigationBarShowLoginButton){
        ApplicationProperties.NAVIGATION_BAR_SHOW_LOGIN_BUTTON = navigationBarShowLoginButton;
    }
}
