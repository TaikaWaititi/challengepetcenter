package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.LoginRequestDTO;
import com.fiap.challengepetcenter.dto.response.LoginResponseDTO;
import com.fiap.challengepetcenter.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );
        String token = jwtService.gerarToken(
                (UserDetails) authentication.getPrincipal()
        );
        return new LoginResponseDTO(token);
    }

}