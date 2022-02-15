package cz.cvut.kbss.ear.project.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.ear.project.rest.dto.LoginDTO;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response
    ) throws AuthenticationException {
        String username, password;
        try {
            LoginDTO loginCredentials = new ObjectMapper().readValue(request.getInputStream(),
                LoginDTO.class);
            username = loginCredentials.getUsername();
            password = loginCredentials.getPassword();
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

        // Create new Spring Authentication token and log in user
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authResult
    ) throws IOException, ServletException {
        // Get the logged-in user
        User user = (User) authResult.getPrincipal();

        // Create an JWT token for the logged-in user
        Algorithm algorithm = Algorithm.HMAC256("EAR_SECRET".getBytes()); // TODO: Secret should not be hardcoded
        String accessToken = JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes
            .withIssuer(request.getRequestURL().toString())
            .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .sign(algorithm);
        String refreshToken = JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000)) // 2 weeks
            .withIssuer(request.getRequestURL().toString())
            .sign(algorithm);

        // Build the response
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
