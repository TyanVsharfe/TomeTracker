package com.tometracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class RestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationManager authenticationManager;
    private final RememberMeServices rememberMeServices;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public RestAuthenticationFilter(AuthenticationManager authenticationManager, RememberMeServices rememberMeServices) {
        super(new AntPathRequestMatcher("/users/login", "POST"));
        this.authenticationManager = authenticationManager;
        this.rememberMeServices = rememberMeServices;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            throw new AuthenticationException("Username or password is missing") {};
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        request.getSession(true);

        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

        String rememberMe = request.getParameter("remember-me");
        if ("true".equals(rememberMe) || "on".equals(rememberMe)) {
            rememberMeServices.loginSuccess(request, response, authResult);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        response.getWriter().write("{\"message\": \"Login successful\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Invalid credentials: " + failed.getMessage() + "\"}");
    }
}
