package com.martin.article.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martin.article.form.LoginForm;
import com.martin.article.globals.SecurityConstants;
import com.martin.article.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(SecurityConstants.SECRET),
            SignatureAlgorithm.HS256.getJcaName());

    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager manager){
        this.authenticationManager = manager;
        setFilterProcessesUrl("/api/token");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try{
            LoginForm creds = new ObjectMapper().readValue(request.getInputStream(), LoginForm.class);

            var usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(creds.getUsername(),
                    creds.getPassword(), new ArrayList<>());

            return authenticationManager.authenticate(usernamePasswordAuthentication);
        } catch (IOException e) {
            throw new RuntimeException("Could not read request " + e);
        }
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication){
        User user = (User) authentication.getPrincipal();

        String token = Jwts.builder()
                .claim("username", user.getUsername())
                .setSubject(user.getUsername())
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, hmacKey)
                .compact();

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    }
}
