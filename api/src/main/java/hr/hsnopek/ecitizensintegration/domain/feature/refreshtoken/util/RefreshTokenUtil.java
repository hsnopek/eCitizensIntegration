package hr.hsnopek.ecitizensintegration.domain.feature.refreshtoken.util;

import javax.servlet.http.HttpServletResponse;

public class RefreshTokenUtil {


    /**
     * Sets refresh token in http-only cookie.
     */

    public synchronized static void setRefreshTokenInCookie(HttpServletResponse httpResponse, String refreshToken) {

        if(refreshToken == null) {
            httpResponse.setHeader(
                    "Set-Cookie",
                    String.format("refreshToken=null;Path=/; Max-Age=0")
            );
        } else {
            httpResponse.setHeader(
                    "Set-Cookie",
                    String.format("refreshToken=%s;Path=/;", refreshToken)
            );
        }
    }
}
