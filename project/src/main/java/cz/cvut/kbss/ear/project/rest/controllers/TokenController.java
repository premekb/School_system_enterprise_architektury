package cz.cvut.kbss.ear.project.rest.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.ear.project.exception.TokenException;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.rest.dto.LoginDTO;
import cz.cvut.kbss.ear.project.service.ParallelService;
import cz.cvut.kbss.ear.project.service.SemesterService;
import cz.cvut.kbss.ear.project.service.UserService;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class TokenController {
    private final UserService userService;

    @Autowired
    public TokenController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Get the JWT refreshToken and decode it
            String refreshToken;
            Map<String, String> credentials = new ObjectMapper().readValue(request.getInputStream(),
                Map.class);
            refreshToken = credentials.get("refreshToken");
            Algorithm algorithm = Algorithm.HMAC256("EAR_SECRET".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            // Get the username from the decoded JWT
            String username = decodedJWT.getSubject();
            User user = userService.findByUsername(username);

            String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", List.of(user.getRole().getName()))
                .sign(algorithm);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (Exception exception) {
            response.setHeader("error", exception.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            // Build the error response
            Map<String, String> tokens = new HashMap<>();
            tokens.put("error", exception.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
    }
}
