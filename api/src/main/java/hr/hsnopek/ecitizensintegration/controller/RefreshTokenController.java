package hr.hsnopek.ecitizensintegration.controller;

import hr.hsnopek.ecitizensintegration.domain.feature.refreshtoken.service.RefreshTokenService;
import hr.hsnopek.ecitizensintegration.domain.feature.refreshtoken.util.RefreshTokenUtil;
import hr.hsnopek.ecitizensintegration.general.model.*;
import hr.hsnopek.ecitizensintegration.security.exceptions.RefreshTokenInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class RefreshTokenController {

    final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshJwtToken(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        AuthenticateUserResponse authenticateUserResponseDTO;
        try {
            authenticateUserResponseDTO = refreshTokenService.refreshToken(httpServletRequest, httpServletResponse);
            RefreshTokenUtil.setRefreshTokenInCookie(httpServletResponse, authenticateUserResponseDTO.getRefreshToken());
        } catch(RefreshTokenInvalidException ree) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                    httpServletRequest.getRequestURI(),
                    ree.getMessage(),
                    ErrorCodes.REFRESH_TOKEN_INVALID,
                    LocalDateTime.now())
            ) ;
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(authenticateUserResponseDTO);
    }

    /**
     * Revokes refresh token and removes it from client cookie.
     *
     * @param httpServletRequest httpServletRequest
     * @param httpServletResponse httpServletResponse
     *
     * @return {@link RevokeTokenResponse}
     */
    @PostMapping("/revoke-token")
    public ResponseEntity<RevokeTokenResponse> revokeToken(
            @RequestBody RevokeTokenRequest revokeTokenRequest,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        refreshTokenService.revokeRefreshToken(httpServletRequest, revokeTokenRequest);
        RefreshTokenUtil.setRefreshTokenInCookie(httpServletResponse, null);

        return ResponseEntity.status(HttpStatus.OK).body(new RevokeTokenResponse(revokeTokenRequest.getToken(), true));
    }



}
