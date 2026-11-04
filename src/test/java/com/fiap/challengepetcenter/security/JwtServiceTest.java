package com.fiap.challengepetcenter.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService service() {
        byte[] key = new byte[48];
        new SecureRandom().nextBytes(key);
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "secret", Base64.getEncoder().encodeToString(key));
        return service;
    }

    @Test
    void tokenAssinadoRecuperaIdentidade() {
        JwtService service = service();
        var user = User.withUsername("tutor@example.test").password("unused").roles("TUTOR").build();
        assertEquals(user.getUsername(), service.extrairUsername(service.gerarToken(user)));
    }

    @Test
    void tokenAssinadoComOutraChaveEhRejeitado() {
        var user = User.withUsername("tutor@example.test").password("unused").roles("TUTOR").build();
        String token = service().gerarToken(user);
        assertThrows(JwtException.class, () -> service().extrairUsername(token));
    }
}
