package hr.hsnopek.ecitizensintegration.security.providers;

import hr.hsnopek.ecitizensintegration.general.localization.Message;
import hr.hsnopek.ecitizensintegration.general.service.Translator;
import hr.hsnopek.ecitizensintegration.security.UserPrincipal;
import hr.hsnopek.ecitizensintegration.security.authenticationtokens.JwtAuthenticationToken;
import hr.hsnopek.ecitizensintegration.security.exceptions.AccessTokenInvalidException;
import hr.hsnopek.ecitizensintegration.security.exceptions.RefreshTokenInvalidException;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil.TokenType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JWTTokenUtil jwtTokenUtil;

    public JwtAuthenticationProvider(JWTTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = new JWTTokenUtil();
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        String jwt = (String) auth.getPrincipal();
        String tokenType = StringUtils.EMPTY;
        JwtAuthenticationToken jwtAuthenticationToken;
        try {

            tokenType = jwtTokenUtil.getTokenTypeFromJWT(jwt);

            jwtTokenUtil.validateToken(jwt);
        } catch (Exception e) {
            if(tokenType.equals(TokenType.REFRESH_TOKEN.toString())) {
                throw new RefreshTokenInvalidException(Translator.toLocale(Message.ERROR_AUTHENTICATION_REFRESH_TOKEN_NOT_VALID), e);
            } else {
                throw new AccessTokenInvalidException(Translator.toLocale(Message.ERROR_AUTHENTICATION_ACCESS_TOKEN_NOT_VALID), e);
            }
        }

        String username = jwtTokenUtil.getUsernameFromJWT(jwt);
        String authorities = jwtTokenUtil.getAuthoritiesFromJWT(jwt);

        List<SimpleGrantedAuthority> authorityList = Arrays.stream(authorities.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserPrincipal principal = new UserPrincipal(
                username,
                authorityList,
                jwt);

        jwtAuthenticationToken = new JwtAuthenticationToken(principal, authorityList);
        jwtAuthenticationToken.setAuthenticated(true);

        return jwtAuthenticationToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }
}


