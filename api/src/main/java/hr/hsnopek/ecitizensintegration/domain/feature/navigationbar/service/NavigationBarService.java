package hr.hsnopek.ecitizensintegration.domain.feature.navigationbar.service;

import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import hr.hsnopek.ecitizensintegration.domain.feature.user.dto.UserDTO;
import hr.hsnopek.ecitizensintegration.general.util.SecurityUtils;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NavigationBarService {

    private final JWTTokenUtil jwtTokenUtil;

    public NavigationBarService(JWTTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public Map<String, String> getNavigationBarData() {
        UserDTO userDTO = jwtTokenUtil.getUserDataFromJWT(SecurityUtils.getTokenFromSpringSecurityContext());
        Map<String, String> result = new HashMap<>();
        result.put("navToken", userDTO.getNavToken());
        result.put("messageId", userDTO.getInResponseTo());
        result.put("showEntitySearch", ApplicationProperties.NAVIGATION_BAR_SHOW_ENTITY_SEARCH);
        result.put("showPersons", ApplicationProperties.NAVIGATION_BAR_SHOW_PERSONS);
        result.put("showEntities", ApplicationProperties.NAVIGATION_BAR_SHOW_ENTITIES);
        result.put("showVisionImpaired", ApplicationProperties.NAVIGATION_BAR_SHOW_VISION_IMPAIRED);
        result.put("showDyslexia", ApplicationProperties.NAVIGATION_BAR_SHOW_DYSLEXIA);
        result.put("showFontResize", ApplicationProperties.NAVIGATION_BAR_SHOW_FONT_RESIZE);
        result.put("showLoginButton", ApplicationProperties.NAVIGATION_BAR_SHOW_LOGIN_BUTTON);
        return result;
    }
}
