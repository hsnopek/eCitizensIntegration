package hr.hsnopek.ecitizensintegration.domain.feature.authorization.service;

import authorizationrequestv3.ServiceRequestType;
import authorizationresponsev3.ServiceResponseType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.client.AuthorizationServiceClient;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.mapper.PermissionTypeMapper;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.model.RegistrationFormSubmitParams;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.model.SimplePermissionType;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.util.DigitalSignatureUtil;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.util.RegistrationFormUtilImpl;
import hr.hsnopek.ecitizensintegration.domain.feature.person.entity.Person;
import hr.hsnopek.ecitizensintegration.domain.feature.person.service.PersonService;
import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import hr.hsnopek.ecitizensintegration.domain.feature.user.mapper.UserDtoMapper;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.entity.UserSession;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;
import roauthorizationapischema.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorizationService {

    private final AuthorizationServiceClient authorizationServiceClient;
    private final DigitalSignatureUtil digitalSignatureUtil;
    private final RegistrationFormUtilImpl registrationFormUtil;
    private final ObjectMapper objectMapper;
    private final JWTTokenUtil jwtTokenUtil;
    private final PersonService personService;

    public AuthorizationService(
            AuthorizationServiceClient authorizationServiceClient, DigitalSignatureUtil digitalSignatureUtil,
            RegistrationFormUtilImpl registrationFormUtil, ObjectMapper objectMapper, JWTTokenUtil jwtTokenUtil,
            PersonService personService) {
        this.authorizationServiceClient = authorizationServiceClient;
        this.digitalSignatureUtil = digitalSignatureUtil;
        this.registrationFormUtil = registrationFormUtil;
        this.objectMapper = objectMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.personService = personService;
    }

    public ServiceRequestType createServiceRequest(String b64Param) {
        byte[] requestBytes = Base64.getMimeDecoder().decode(b64Param);
        return registrationFormUtil.unmarshalServiceRequest(
                new String(Base64.getMimeDecoder().decode(b64Param.getBytes())));
    }

    public String createServiceRequestFormSubmitFrontendParams(
            String responseUrl, String cancelUrl, ServiceRequestType serviceRequestType)
            throws JsonProcessingException {

        RegistrationFormSubmitParams registrationFormSubmitParams = new RegistrationFormSubmitParams();
        registrationFormSubmitParams.setServiceRequestId(serviceRequestType.getId());
        registrationFormSubmitParams.setResponseUrl(responseUrl);
        registrationFormSubmitParams.setCancelUrl(cancelUrl);

        assert serviceRequestType.getAuthorizationInfo() != null;

        if(serviceRequestType.getAuthorizationInfo().getActivePermissions() != null){
            registrationFormSubmitParams.setPermissions(
                    PermissionTypeMapper.mapSimplePermissionType(serviceRequestType.getAuthorizationInfo().getActivePermissions().getPermission()));
        }

        registrationFormSubmitParams.setFromEntityFirstName(serviceRequestType.getAuthorizationInfo().getFromEntity().getPerson().getLocalPerson().getFirstName());
        registrationFormSubmitParams.setFromEntityLastName(serviceRequestType.getAuthorizationInfo().getFromEntity().getPerson().getLocalPerson().getLastName());
        registrationFormSubmitParams.setFromEntityLegalName(serviceRequestType.getAuthorizationInfo().getFromEntity().getLegal().getName());
        registrationFormSubmitParams.setFromEntityOib(serviceRequestType.getAuthorizationInfo().getFromEntity().getPerson().getLocalPerson().getOIB());

        registrationFormSubmitParams.setForEntityIps(serviceRequestType.getAuthorizationInfo().getForEntity().getLegal().getName());
        registrationFormSubmitParams.setForEntityIps(serviceRequestType.getAuthorizationInfo().getForEntity().getLegal().getJips().getIPS());

        registrationFormSubmitParams.setToEntityFirstName(serviceRequestType.getAuthorizationInfo().getToEntity().getPerson().getFirstName());
        registrationFormSubmitParams.setToEntityLastName(serviceRequestType.getAuthorizationInfo().getToEntity().getPerson().getLastName());
        registrationFormSubmitParams.setToEntityLegalName(serviceRequestType.getAuthorizationInfo().getToEntity().getLegal().getName());
        registrationFormSubmitParams.setToEntityOib(serviceRequestType.getAuthorizationInfo().getToEntity().getPerson().getOIB());

        return Base64.getEncoder()
                .encodeToString(
                        objectMapper.writeValueAsString(registrationFormSubmitParams).getBytes(StandardCharsets.UTF_8)
                );
    }

    public String createServiceResponse(String serviceRequestId, String b64permissions)
            throws JsonProcessingException, DatatypeConfigurationException {
        // init serviceRequestType
        authorizationresponsev3.ServiceRequestType serviceRequestType = new authorizationresponsev3.ServiceRequestType();
        serviceRequestType.setId(serviceRequestId);
        // map permissions from frontend
        String permissionsB64Decoded = new String(Base64.getDecoder().decode(b64permissions));
        List<SimplePermissionType> simplePermissionTypes = objectMapper.readValue(permissionsB64Decoded, new TypeReference<>() {});
        List<authorizationresponsev3.PermissionType> permissionTypes =
                PermissionTypeMapper.mapPermissionType(simplePermissionTypes);
        // generate response
        ServiceResponseType tResponse = registrationFormUtil.createResponse(serviceRequestType, permissionTypes);
        // sign and convert to base64
        byte[] tXMLResponse = digitalSignatureUtil.signFile(registrationFormUtil.marshalServiceResponse(tResponse));
        return Base64.getEncoder().encodeToString(tXMLResponse);
    }

    public String actAsEntityAndGenerateNewAccessToken(
            String ident, String forLegalIps, Integer forLegalIzvor, String toLegalIps, Integer toLegalIzvor,
            HttpServletRequest httpServletRequest) throws IOException, UnrecoverableKeyException, JAXBException,
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        // TODO: If userAgent was changed after login create new user (when using developer tools user agent changes),

        Cookie refreshTokenCookie = WebUtils.getCookie(httpServletRequest, "refreshToken");
        if (refreshTokenCookie != null) {
            String jwt = refreshTokenCookie.getValue();
            String tid = jwtTokenUtil.getUsernameFromJWT(jwt);
            Optional<Person> optionalPerson = personService.findByTid(tid);
            String userAgent = httpServletRequest.getHeader("User-Agent");

            if (optionalPerson.isPresent()) {

                User user = optionalPerson.get().getUsers().stream()
                        .filter(x -> x.getIdent().equals(ident))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("User ident not found!"));

                UserSession userSession = Objects.requireNonNull(user.getUserSessions()
                        .stream()
                        .filter(session -> session.getDeviceId().equalsIgnoreCase(userAgent))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("User session not found!")));

                SignedAuthorizationUnionPermissionResponseType response = sendAuthorizationRequest(
                        user.getOib(), user.getDn(), forLegalIps, forLegalIzvor, toLegalIps, toLegalIzvor, userSession.getSessionId());

                RepresentativeLegalItemDataType.Functions functions = null;
                List<PermissionType> permissionList = null;

                if (response.getRepresentation() != null &&
                        response.getRepresentation().getDataEntityFor() != null &&
                        response.getRepresentation().getDataEntityFor().getDataLegal() != null) {
                    functions = response.getRepresentation().getDataEntityFor().getDataLegal().getFunctions();
                }

                if (response.getAuthorization() != null && response.getAuthorization().getPermissions() != null) {
                    permissionList = response.getAuthorization().getPermissions().getPermission();
                }

                String userRoles = user.getRoles()
                        .stream()
                        .map(role -> role.getRoleName().toString())
                        .collect(Collectors.joining(","));

                String accessToken = jwtTokenUtil.generateAccessToken(
                        user.getPerson().getTid(),
                        UserDtoMapper.map(user, userRoles, userAgent),
                        userRoles,
                        functions,
                        permissionList,
                        true
                );

                return accessToken;
            }
        }
        return null;
    }

    // Authorization data
    public SignedAuthorizationUnionPermissionResponseType sendAuthorizationRequest(String oib, String dn, String forLegalIps,
            Integer forLegalIzvor, String toLegalIps, Integer toLegalIzvor, String sesijaId)
            throws JAXBException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException {

        AuthorizationUnionPermissionRequestType authorizationUnionPermissionRequest = new AuthorizationUnionPermissionRequestType();

        authorizationUnionPermissionRequest.setSesijaId(sesijaId);
        authorizationUnionPermissionRequest.setPersonOIB(oib);

        if(StringUtils.isNotBlank(dn))
            authorizationUnionPermissionRequest.setCertificateDn(dn);

        // Describes with what kind of authentication means user logs-in

        // Bussiness certificate - parameters IPS and IZVOR_REG are user that are returned by navigation NIAS
        // (oib is bussines subject oib from company that issued the certificate)
        // Personal certificate - parameters ToLegalIps and ToLegalIzvor_reg are user that are returned by navigation bar
        // ePass - element is ommited; we are checking rights for person as a citizen

        if(toLegalIps != null && toLegalIzvor != null) {
            JipsType toJipsType = new JipsType();
            toJipsType.setIPS(toLegalIps);
            toJipsType.setIZVORREG(toLegalIzvor);
            authorizationUnionPermissionRequest.setJipsTo(toJipsType);
        }

        // IdentifiersFor - for whom we want the permisson for
        // If PersonOib is used, then we want permissions for citizen
        // If LegalJips is used, then we want permission for bussines subject

        PersonOrLegalIdentifierType personOrLegalIdentifier = new PersonOrLegalIdentifierType();
        if(forLegalIps != null && forLegalIzvor != null) {
            JipsType forJipsType = new JipsType();
            forJipsType.setIPS(forLegalIps);
            forJipsType.setIZVORREG(forLegalIzvor);

            personOrLegalIdentifier.setLegalJips(forJipsType);
        } else {
            personOrLegalIdentifier.setPersonOib(oib);
        }

        authorizationUnionPermissionRequest.setIdentfiersFor(personOrLegalIdentifier);
        authorizationUnionPermissionRequest.setId(UUID.randomUUID().toString());

        String request = marshal(authorizationUnionPermissionRequest);
        return authorizationServiceClient.sendAuthorizationRequest(request);
    }

    private String marshal(AuthorizationUnionPermissionRequestType authorizationUnionPermissionRequest) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(AuthorizationUnionPermissionRequestType.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        try(OutputStream bos = new ByteArrayOutputStream()){

            javax.xml.namespace.QName qName =
                    new QName("http://eovlastenja.fina.hr/RoAuthUnionApi/v2","AuthorizationUnionPermissionRequest");

            JAXBElement<AuthorizationUnionPermissionRequestType> authorizationUnionPermissionRequestTypeJAXBElement =
                    new JAXBElement<>(
                            qName,
                            AuthorizationUnionPermissionRequestType.class,
                            authorizationUnionPermissionRequest);

            marshaller.marshal(authorizationUnionPermissionRequestTypeJAXBElement, bos);
            return bos.toString();
        } catch (Exception e){
            throw e;
        }
    }
}
