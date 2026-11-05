package com.fiap.challengepetcenter.security;

import com.fiap.challengepetcenter.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final ApiAuthenticationHandler authenticationHandler;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService,
                                   ApiAuthenticationHandler authenticationHandler) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationHandler = authenticationHandler;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Um token antigo nao deve impedir novo login ou cadastro publico.
        String path = request.getServletPath();
        return "POST".equals(request.getMethod())
                && ("/api/auth/login".equals(path) || "/api/users".equals(path));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extrairUsername(token);
            if (username == null || username.isBlank()) {
                throw new BadCredentialsException("Token sem usuario");
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!userDetails.isEnabled()) {
                throw new BadCredentialsException("Usuario inativo");
            }
            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException | BadCredentialsException exception) {
            SecurityContextHolder.clearContext();
            authenticationHandler.commence(request, response,
                    new BadCredentialsException("Token invalido ou expirado", exception));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
