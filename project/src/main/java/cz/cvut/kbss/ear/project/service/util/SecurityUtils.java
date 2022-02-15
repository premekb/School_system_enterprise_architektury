package cz.cvut.kbss.ear.project.service.util;

import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private static UserService userService;

    @Autowired
    private SecurityUtils(UserService userService) {
        SecurityUtils.userService = userService;
    }

    public static User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername((String) authentication.getPrincipal());
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
