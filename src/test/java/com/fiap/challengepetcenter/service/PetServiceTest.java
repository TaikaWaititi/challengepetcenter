package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.PetRequestDTO;
import com.fiap.challengepetcenter.exception.DiarioEntradaComDependenciasException;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {
    @Mock PetRepository pets;
    @Mock UserRepository users;
    @Mock DiarioEntradaRepository diarios;
    PetService service;
    User tutor;
    Pet pet;

    @BeforeEach
    void setup() {
        service = new PetService(pets, users, diarios);
        tutor = new User();
        tutor.setId(10L);
        tutor.setNome("Tutor CI");
        pet = new Pet();
        pet.setId(20L);
        pet.setUser(tutor);
        pet.setNome("Luna");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("ci@example.test", null));
        when(users.findByEmail("ci@example.test")).thenReturn(Optional.of(tutor));
    }

    @AfterEach
    void cleanup() { SecurityContextHolder.clearContext(); }

    @Test
    void cadastroUsaTutorAutenticado() {
        when(pets.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet saved = invocation.getArgument(0);
            assertSame(tutor, saved.getUser());
            saved.setId(20L);
            return saved;
        });
        var result = service.salvar(request("Luna"));
        assertEquals(20L, result.id());
        assertEquals("Luna", result.nome());
    }

    @Test
    void consultaRetornaPetDoTutor() {
        when(pets.findById(20L)).thenReturn(Optional.of(pet));
        assertEquals("Luna", service.buscarPorId(20L).nome());
    }

    @Test
    void consultaImpedeAcessoAPetDeOutroTutor() {
        tutor.setId(99L);
        User owner = new User();
        owner.setId(10L);
        pet.setUser(owner);
        when(pets.findById(20L)).thenReturn(Optional.of(pet));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(20L));
    }

    @Test
    void atualizacaoPersisteNovosDados() {
        when(pets.findById(20L)).thenReturn(Optional.of(pet));
        when(pets.save(pet)).thenReturn(pet);
        assertEquals("Luna atualizada", service.atualizar(20L, request("Luna atualizada")).nome());
        verify(pets).save(pet);
    }

    @Test
    void exclusaoBloqueiaPetComDiario() {
        when(pets.findById(20L)).thenReturn(Optional.of(pet));
        when(diarios.existsByPetId(20L)).thenReturn(true);
        assertThrows(DiarioEntradaComDependenciasException.class, () -> service.deletar(20L));
        verify(pets, never()).deleteById(any());
    }

    @Test
    void exclusaoRemovePetSemDependencias() {
        when(pets.findById(20L)).thenReturn(Optional.of(pet));
        service.deletar(20L);
        verify(pets).deleteById(20L);
    }

    private PetRequestDTO request(String nome) {
        return new PetRequestDTO(nome, "Cachorro", "SRD", LocalDate.of(2021, 1, 10), "Teste CI");
    }
}
