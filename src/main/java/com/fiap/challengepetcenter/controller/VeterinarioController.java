package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.dto.request.VeterinarioRequestDTO;
import com.fiap.challengepetcenter.dto.response.VeterinarioResponseDTO;
import com.fiap.challengepetcenter.service.VeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veterinarios")
@Tag(name = "Veterinários", description = "Endpoints para gerenciamento de veterinários")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @PostMapping
    @Operation(
            summary = "Criar veterinário",
            description = "Cria um novo veterinário associado a um usuário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Veterinário criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<VeterinarioResponseDTO> criar(@Valid @RequestBody VeterinarioRequestDTO requestDTO) {
        VeterinarioResponseDTO novoVeterinario = veterinarioService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoVeterinario);
    }

    @GetMapping
    @Operation(
            summary = "Listar veterinarios",
            description = "Retorna uma lista completa de todos os veterinários cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de veterinarios retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeterinarioResponseDTO.class)
            )
    )
    public ResponseEntity<Page<VeterinarioResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<VeterinarioResponseDTO> veterinarios = veterinarioService.listarTodos(pageable);
        return ResponseEntity.ok(veterinarios);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar veterinario por ID",
            description = "Retorna um veterinario específico baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Veterinario encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veterinario não encontrado"
            )
    })
    public ResponseEntity<VeterinarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.buscarPorId(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Buscar pets por usuário",
            description = "Retorna todos os pets associados a um usuário específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pets encontrados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public ResponseEntity<Page<VeterinarioResponseDTO>> buscarPorUserId(
            @PathVariable Long userId,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<VeterinarioResponseDTO> users = veterinarioService.buscarPorUserId(userId, pageable);
        return ResponseEntity.ok(users);
    }


    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar pet",
            description = "Atualiza os dados de um pet existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pet atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<VeterinarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody VeterinarioRequestDTO requestDTO) {
        VeterinarioResponseDTO veterinarioAtualizado = veterinarioService.atualizar(id, requestDTO);
        return ResponseEntity.ok(veterinarioAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar pet",
            description = "Remove um pet do sistema baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Pet removido com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet não encontrado"
            )
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        veterinarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
