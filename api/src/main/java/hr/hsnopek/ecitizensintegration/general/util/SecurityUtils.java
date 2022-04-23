package hr.hsnopek.ecitizensintegration.general.util;

import hr.hsnopek.ecitizensintegration.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static UserPrincipal getPrincipalFromSpringSecurityContext(){
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String getUsernameFromSpringSecurityContext(){
        return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    public static String getTokenFromSpringSecurityContext(){
        return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getToken();
    }
}
