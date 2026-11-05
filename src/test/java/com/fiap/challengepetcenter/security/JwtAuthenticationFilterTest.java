package com.fiap.challengepetcenter.security;

import com.fiap.challengepetcenter.service.CustomUserDetailsService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    final JwtService jwt = mock(JwtService.class);
    final CustomUserDetailsService users = mock(CustomUserDetailsService.class);
    final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwt, users, new ApiAuthenticationHandler());
    final FilterChain chain = mock(FilterChain.class);

    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @Test
    void tokenInvalidoRetorna401ComOrientacaoSemExecutarCrud() throws Exception {
        var request = request("/api/pets");
        var response = new MockHttpServletResponse();
        when(jwt.extrairUsername("invalid")).thenThrow(new MalformedJwtException("invalido"));
        filter.doFilter(request, response, chain);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("/api/auth/login"));
        verifyNoInteractions(chain, users);
    }

    @Test
    void loginPermanecePublicoMesmoComTokenAntigo() throws Exception {
        var request = request("/api/auth/login");
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwt, users);
    }

    @Test
    void tokenValidoMantemPerfilTutor() throws Exception {
        var request = request("/api/pets");
        var response = new MockHttpServletResponse();
        when(jwt.extrairUsername("invalid")).thenReturn("tutor@example.test");
        when(users.loadUserByUsername("tutor@example.test")).thenReturn(
                User.withUsername("tutor@example.test").password("unused").roles("TUTOR").build());
        filter.doFilter(request, response, chain);
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_TUTOR")));
        verify(chain).doFilter(request, response);
    }

    @Test
    void usuarioInativoNaoConsegueUsarTokenAntigo() throws Exception {
        var request = request("/api/pets");
        var response = new MockHttpServletResponse();
        when(jwt.extrairUsername("invalid")).thenReturn("tutor@example.test");
        when(users.loadUserByUsername("tutor@example.test")).thenReturn(
                User.withUsername("tutor@example.test").password("unused").roles("TUTOR").disabled(true).build());
        filter.doFilter(request, response, chain);
        assertEquals(401, response.getStatus());
        verifyNoInteractions(chain);
    }

    private MockHttpServletRequest request(String path) {
        var request = new MockHttpServletRequest("POST", path);
        request.setServletPath(path);
        request.addHeader("Authorization", "Bearer invalid");
        return request;
    }
}
