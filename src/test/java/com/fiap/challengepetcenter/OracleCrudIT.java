package com.fiap.challengepetcenter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Executado apenas com -Poracle-it, contra um Oracle de testes descartavel. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OracleCrudIT {
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    final JsonMapper json = JsonMapper.builder().build();

    @Test
    void crudAutenticadoPersisteCadaOperacaoNoOracle() throws Exception {
        String email = "ci." + UUID.randomUUID() + "@example.test";
        // Credencial exclusivamente de massa de teste, sem acesso ao ambiente da entrega.
        String password = UUID.randomUUID().toString().substring(0, 8);
        long userId = 0;
        long petId = 0;
        try {
            var createdUser = send("POST", "/api/users", """
                    {"nome":"Tutor Integracao", "email":"%s", "senha":"%s",
                     "telefone":"11900000000", "tipoUsuario":"TUTOR"}
                    """.formatted(email, password), null, 201);
            userId = createdUser.get("id").asLong();
            String token = send("POST", "/api/auth/login",
                    """
                    {"email":"%s","senha":"%s"}
                    """.formatted(email, password), null, 200).get("token").asText();
            String path = "/api/pets/";
            var pet = send("POST", "/api/pets", petBody("Luna CI"), token, 201);
            petId = pet.get("id").asLong();
            assertEquals("Luna CI", jdbc.queryForObject("SELECT nome FROM pets WHERE id = ?", String.class, petId));
            assertEquals("Luna CI", send("GET", path + petId, null, token, 200).get("nome").asText());
            send("PUT", path + petId, petBody("Luna CD"), token, 200);
            assertEquals("Luna CD", jdbc.queryForObject("SELECT nome FROM pets WHERE id = ?", String.class, petId));
            send("DELETE", path + petId, null, token, 204);
            assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM pets WHERE id = ?", Integer.class, petId));
        } finally {
            if (petId != 0) jdbc.update("DELETE FROM pets WHERE id = ?", petId);
            if (userId != 0) jdbc.update("DELETE FROM users WHERE id = ?", userId);
        }
    }

    private String petBody(String nome) {
        return """
                {"nome":"%s","especie":"Cachorro","raca":"SRD",
                 "dataNascimento":"2021-01-10","observacoes":"Teste CI isolado"}
                """.formatted(nome);
    }

    private JsonNode send(String method, String path, String body, String token, int expected) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(30)).header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        if (token != null) request.header("Authorization", "Bearer " + token);
        var response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(expected, response.statusCode(), method + " " + path);
        return response.body().isBlank() ? json.nullNode() : json.readTree(response.body());
    }
}
