package hr.hsnopek.ecitizensintegration.security.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import hr.hsnopek.ecitizensintegration.domain.feature.user.dto.UserDTO;
import hr.hsnopek.ecitizensintegration.security.UserPrincipal;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import roauthorizationapischema.PermissionType;
import roauthorizationapischema.RepresentativeLegalItemDataType;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTTokenUtil {

    private static final String AUTHORITIES_CLAIM = "authorities";
    private static final String ACTIVE_CLAIM = "active";
    private static final String LOGGED_IN_CLAIM = "loggedIn";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String LEGAL_FUNCTIONS = "legalFunctions";
    private static final String PERMISSIONS = "permissions";
    private static final String USER_DATA = "userData";

    @Autowired
    ObjectMapper objectMapper;

    public String generateRefreshToken(String subject) {
        Instant expiryDate = Instant.now().plusMillis(ApplicationProperties.REFRESH_TOKEN_EXPIRATION_TIME);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiryDate))
                .claim(TOKEN_TYPE_CLAIM, TokenType.REFRESH_TOKEN.toString())
                .signWith(SignatureAlgorithm.HS512, ApplicationProperties.JWT_SECRET)
                .compact();
    }

    /**
     * Generates access token for authenticated user.
     *
     * @param subject authenticated user principal
     * @param active true if user activated his account via e-mail
     *
     * @return {@link String}
     */
    public String generateAccessToken(
            String subject,
            UserDTO userDTO,
            String authorities,
            RepresentativeLegalItemDataType.Functions legalFunctions,
            List<PermissionType> permissions,
            boolean active) throws JsonProcessingException {

        Instant expiryDate = Instant.now().plusMillis(ApplicationProperties.ACCESS_TOKEN_EXPIRATION_TIME);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiryDate))
                .signWith(SignatureAlgorithm.HS512, ApplicationProperties.JWT_SECRET)
                .claim(USER_DATA, userDTO)
                .claim(AUTHORITIES_CLAIM, authorities)
                .claim(LEGAL_FUNCTIONS, legalFunctions)
                .claim(PERMISSIONS, permissions)
                .claim(ACTIVE_CLAIM, active)
                .claim(TOKEN_TYPE_CLAIM, TokenType.ACCESS_TOKEN.toString())
                .compact();
    }

    /**
     * Extracts token type claim from token.
     * @param token jwt
     *
     * @return {@link String} claim
     */
    public String getTokenTypeFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(ApplicationProperties.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get(TOKEN_TYPE_CLAIM);
    }

    /**
     * Extracts subject claim from token.
     * @param token jwt
     *
     * @return {@link String} claim
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(ApplicationProperties.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Extracts JWT ID from token.
     * @param token jwt
     *
     * @return {@link String} claim
     */
    public String getJtiFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(ApplicationProperties.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getId();
    }


    /**
     * Extracts authorized claim from token. If 2FA has been enabled, token is
     * authorized only if user account is active (registration mail confirmed) and
     * OTP code has been verified.
     *
     * @param token jwt
     *
     * @return {@link Boolean}
     */
    public boolean getAuthorizedClaimFromJWT(String token) throws JsonMappingException, JsonProcessingException {
        Claims claims = Jwts.parser()
                .setSigningKey(ApplicationProperties.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return (boolean) claims.get(LOGGED_IN_CLAIM);
    }

    /**
     * Extracts active claim from token. Returns true if account activated via e-mail.
     *
     * @param token jwt
     *
     * @return {@link Boolean}
     */
    public boolean getActiveClaimFromJWT(String token) throws JsonMappingException, JsonProcessingException {
        Claims claims = Jwts.parser()
                .setSigningKey(ApplicationProperties.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return (boolean) claims.get(ACTIVE_CLAIM);
    }

    /**
     * Gets token expiry date.
     * @param token jwt
     *
     * @return {@link Date}
     */
    public Date getTokenExpiryFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(ApplicationProperties.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }


    /**
     * Gets list of {@link UserPrincipal} granted authorities from token.
     * @param token jwt
     *
     * @return {@link List<GrantedAuthority>}
     */
    public String getAuthoritiesFromJWT(String token) {
        Claims claims = null;
        try{
            claims = Jwts.parser()
                    .setSigningKey(ApplicationProperties.JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException expiredJwtException){
            claims = expiredJwtException.getClaims();
        }
        return claims.get(AUTHORITIES_CLAIM).toString();
    }

    /**
     * Returns comma separated {@link UserPrincipal} authorities
     * @param principal authenticated user
     *
     * @return {@link String}
     */
    private String getUserAuthorities(UserPrincipal principal) {
        return principal
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    /**
     * Validates JWT token with server provided secret key.
     *
     * @param token jwt
     * @throws {@link SignatureException}, {@link MalformedJwtException},
     *                {@link ExpiredJwtException}, {@link UnsupportedJwtException},
     *                {@link IllegalArgumentException}
     * @return {@link String}
     */
    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(ApplicationProperties.JWT_SECRET)
                    .parseClaimsJws(token);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException | ExpiredJwtException ex) {
            throw ex;
        }
    }

    public RepresentativeLegalItemDataType.Functions getLegalFunctionsFromJWT(String oldAccessToken) throws IOException {
        Claims claims = null;
        try{
            claims = Jwts.parser()
                    .setSigningKey(ApplicationProperties.JWT_SECRET)
                    .parseClaimsJws(oldAccessToken)
                    .getBody();
        } catch (ExpiredJwtException expiredJwtException){
            claims = expiredJwtException.getClaims();
        }
        return objectMapper.convertValue(claims.get(LEGAL_FUNCTIONS), RepresentativeLegalItemDataType.Functions.class);
    }

    public List<PermissionType> getPermissionFromJWT(String oldAccessToken) throws IOException {
        Claims claims = null;
        try{
            claims = Jwts.parser()
                    .setSigningKey(ApplicationProperties.JWT_SECRET)
                    .parseClaimsJws(oldAccessToken)
                    .getBody();
        } catch (ExpiredJwtException expiredJwtException){
            claims = expiredJwtException.getClaims();
        }
        return objectMapper.convertValue(claims.get(PERMISSIONS), new TypeReference<List<PermissionType>>() {});
    }

    public UserDTO getUserDataFromJWT(String oldAccessToken) {
        Claims claims = null;
        try{
            claims = Jwts.parser()
                    .setSigningKey(ApplicationProperties.JWT_SECRET)
                    .parseClaimsJws(oldAccessToken)
                    .getBody();
        } catch (ExpiredJwtException expiredJwtException){
            claims = expiredJwtException.getClaims();
        }
        return objectMapper.convertValue(claims.get(USER_DATA), UserDTO.class);
    }

    public enum TokenType {

        REFRESH_TOKEN("refresh_token"),
        ACCESS_TOKEN("access_token");

        private final String tokenType;

        TokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public static TokenType fromString(String roleName) throws IllegalArgumentException {
            return Arrays.stream(TokenType.values())
                    .filter(x -> x.tokenType.equals(roleName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + roleName));
        }

        @Override
        public String toString() {
            return tokenType;
        }

    }

}
