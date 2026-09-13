package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.dto.request.AlertaRequestDTO;
import com.fiap.challengepetcenter.dto.response.AlertaResponseDTO;
import com.fiap.challengepetcenter.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "Endpoints para gerenciamento de alertas")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @PostMapping
    @PreAuthorize("hasRole('VETERINARIO')")
    @Operation(
            summary = "Criar alerta",
            description = "Cria um novo alerta para um pet. O veterinário é identificado pelo usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Alerta criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet ou veterinário não encontrado"
            )
    })
    public ResponseEntity<AlertaResponseDTO> criar(@Valid @RequestBody AlertaRequestDTO requestDTO) {
        AlertaResponseDTO novoAlerta = alertaService.salvar(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoAlerta);
    }

    @GetMapping
    @PreAuthorize("hasRole('TUTOR') or hasRole('VETERINARIO')")
    @Operation(
            summary = "Listar alertas",
            description = "Lista todos os alertas cadastrados com paginação."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Alertas listados com sucesso"
            )
    })
    public ResponseEntity<Page<AlertaResponseDTO>> listarTodos(Pageable pageable) {

        return ResponseEntity.ok(
                alertaService.listarTodos(pageable)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TUTOR') or hasRole('VETERINARIO')")
    @Operation(
            summary = "Buscar alerta por ID",
            description = "Busca um alerta específico pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Alerta encontrado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Alerta não encontrado"
            )
    })
    public ResponseEntity<AlertaResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                alertaService.buscarPorId(id)
        );
    }

    @GetMapping("/pet/{petId}")
    @PreAuthorize("hasRole('TUTOR') or hasRole('VETERINARIO')")
    @Operation(
            summary = "Listar alertas de um pet",
            description = "Lista os alertas associados a um pet específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Alertas listados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet não encontrado"
            )
    })
    public ResponseEntity<Page<AlertaResponseDTO>> listarPorPet(
            @PathVariable Long petId,
            Pageable pageable) {

        return ResponseEntity.ok(
                alertaService.listarPorPet(petId, pageable)
        );
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('TUTOR') or hasRole('VETERINARIO')")
    @Operation(
            summary = "Desativar alerta",
            description = "Desativa um alerta existente. O usuário deve ser o tutor do pet ou o veterinário responsável pelo alerta."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Alerta desativado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Alerta não encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para desativar o alerta"
            )
    })
    public ResponseEntity<AlertaResponseDTO> desativar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                alertaService.desativar(id)
        );
    }
}
