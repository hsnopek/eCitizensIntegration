package hr.hsnopek.ecitizensintegration.domain.feature.refreshtoken.service;

import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import hr.hsnopek.ecitizensintegration.domain.feature.user.service.UserService;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.service.UserSessionService;
import hr.hsnopek.ecitizensintegration.general.localization.Message;
import hr.hsnopek.ecitizensintegration.general.model.AuthenticateUserResponse;
import hr.hsnopek.ecitizensintegration.general.model.RevokeTokenRequest;
import hr.hsnopek.ecitizensintegration.general.service.Translator;
import hr.hsnopek.ecitizensintegration.security.exceptions.AccessTokenInvalidException;
import hr.hsnopek.ecitizensintegration.security.exceptions.RefreshTokenInvalidException;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Objects;

@Service
public class RefreshTokenService {

    final JWTTokenUtil jwtTokenUtil;
    final UserService userService;
    final UserSessionService userSessionService;

    public RefreshTokenService(JWTTokenUtil jwtTokenUtil, UserService userService, UserSessionService userSessionService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.userSessionService = userSessionService;
    }

    public AuthenticateUserResponse refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Cookie refreshTokenCookie = WebUtils.getCookie(httpServletRequest, "refreshToken");
        if (Objects.nonNull(refreshTokenCookie)) {
            final String refreshToken = refreshTokenCookie.getValue();
            try {
                jwtTokenUtil.validateToken(refreshToken);

                String authorizationHeader = httpServletRequest.getHeader("Authorization");

                if (StringUtils.isEmpty(authorizationHeader) ||
                        (StringUtils.isNotEmpty(authorizationHeader) && !authorizationHeader.startsWith("Bearer"))){
                    throw new AccessTokenInvalidException("Access token is invalid");
                }

                String oldAccessToken = authorizationHeader.replace("Bearer ", "");

                String accessToken = jwtTokenUtil
                        .generateAccessToken(
                                jwtTokenUtil.getUsernameFromJWT(refreshToken),
                                jwtTokenUtil.getUserDataFromJWT(oldAccessToken),
                                jwtTokenUtil.getAuthoritiesFromJWT(oldAccessToken),
                                jwtTokenUtil.getLegalFunctionsFromJWT(oldAccessToken),
                                jwtTokenUtil.getPermissionFromJWT(oldAccessToken),
                                true);
                return new AuthenticateUserResponse(refreshToken, accessToken);
            } catch (Exception e) {
                throw new RefreshTokenInvalidException(Translator.toLocale(Message.ERROR_AUTHENTICATION_REFRESH_TOKEN_NOT_VALID), e);
            }
        }
        return new AuthenticateUserResponse(null, null);
    }

    @Transactional
    public void revokeRefreshToken(HttpServletRequest httpServletRequest, RevokeTokenRequest revokeTokenRequest) {

        Cookie refreshTokenCookie = WebUtils.getCookie(httpServletRequest, "refreshToken");
        String userAgent = httpServletRequest.getHeader("User-Agent");

        if (Objects.nonNull(refreshTokenCookie)) {
            final String refreshToken = refreshTokenCookie.getValue();
            String principal = jwtTokenUtil.getUsernameFromJWT(refreshToken);
            User user = userService.findByIdent(principal);

            user.getUserSessions()
                    .stream()
                    .filter(device -> device.getDeviceId().equals(userAgent))
                    .findFirst()
                    .ifPresentOrElse((userSession) -> {
                        userSession.setSessionIndex(null);
                        userSessionService.saveUserSession(userSession);
                    }, () -> {
                        throw new RuntimeException("Cannot perform revocation of refresh token because user session does not exist!");
                    });

            user.setActive(false);
        }

    }
}
