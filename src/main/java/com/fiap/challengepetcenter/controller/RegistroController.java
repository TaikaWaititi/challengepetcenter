package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.DTO.*;
import com.fiap.challengepetcenter.service.RegistroService;
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
@RequestMapping("/api/registros")
@Tag(name = "Registros", description = "Endpoints para gerenciamento de registros")
public class RegistroController {

    @Autowired
    private RegistroService registroService;

    @PostMapping
    @Operation(
            summary = "Criar registro",
            description = "Cria um novo registro associado a uma entrada do diário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Registro criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<RegistroResponseDTO> criar(@Valid @RequestBody RegistroRequestDTO requestDTO) {
        RegistroResponseDTO novoRegistro = registroService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    @GetMapping
    @Operation(
            summary = "Listar registros",
            description = "Retorna uma lista completa de todos os registros cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de registros retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegistroResponseDTO.class)
            )
    )

    public ResponseEntity<Page<RegistroResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<RegistroResponseDTO> registros = registroService.listarTodos(pageable);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar registro por ID",
            description = "Retorna um registro específico baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro não encontrado"
            )
    })
    public ResponseEntity<RegistroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(registroService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar registro",
            description = "Atualiza os dados de um registro existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<RegistroResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody RegistroRequestDTO requestDTO) {
        RegistroResponseDTO registroAtualizado = registroService.atualizar(id, requestDTO);
        return ResponseEntity.ok(registroAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar registro",
            description = "Remove um registro do sistema baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Registro removido com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro não encontrado"
            )
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        registroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
