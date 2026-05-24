package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.DTO.DiarioEntradaRequestDTO;
import com.fiap.challengepetcenter.DTO.DiarioEntradaResponseDTO;
import com.fiap.challengepetcenter.service.DiarioEntradaService;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/api/diarioentradas")
@Tag(name = "DiarioEntradas", description = "Endpoints para gerenciamento das entradas no diário")
public class DiarioEntradaController {

    @Autowired
    private DiarioEntradaService diarioEntradaService;

    @PostMapping
    @Operation(
            summary = "Criar entrada no diário",
            description = "Cria uma nova entrada no diário associada a um pet."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Entrada criada com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos")
    })
    public ResponseEntity<DiarioEntradaResponseDTO> criar(@Valid @RequestBody DiarioEntradaRequestDTO requestDTO) {
        DiarioEntradaResponseDTO novoDiarioEntrada = diarioEntradaService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoDiarioEntrada);
    }

    @GetMapping
    @Operation(
            summary = "Listar todas as entradas no diário",
            description = "Retorna uma lista  completa de todas as entradas cadastradas."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de entradas no diário retornada com sucesso!!!",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DiarioEntradaResponseDTO.class))
    )

    public ResponseEntity<Page<DiarioEntradaResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<DiarioEntradaResponseDTO> entradas = diarioEntradaService.listarTodos(pageable);
        return ResponseEntity.ok(entradas);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar entrada por ID",
            description = "Retorna uma entrada específica baseada no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Entrada encontrada com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrada não encontrada")
    })
    public ResponseEntity<DiarioEntradaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(diarioEntradaService.buscarPorId(id));
    }

    // GET http://localhost:8080/api/diarioentradas/data?data=2026-05-11
    @GetMapping("/data")
    @Operation(
            summary = "Buscar entradas por data",
            description = "Retorna todas as entradas cadastradas em uma data específica."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Entradas encontradas com sucesso"
    )
    public ResponseEntity<Page<DiarioEntradaResponseDTO>> buscarPorData(
            @RequestParam LocalDate data,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {

        Page<DiarioEntradaResponseDTO> entradas = diarioEntradaService.buscarPorData(data, pageable);
        return ResponseEntity.ok(entradas);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar entrada do diário",
            description = "Atualiza uma entrada existente baseada no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Entrada atualizada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrada não encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<DiarioEntradaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody DiarioEntradaRequestDTO requestDTO) {
        DiarioEntradaResponseDTO diarioEntradaAtualizado = diarioEntradaService.atualizar(id, requestDTO);
        return ResponseEntity.ok(diarioEntradaAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar entrada do diário",
            description = "Remove uma entrada do diário baseada no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Entrada removida com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrada não encontrada"
            )
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        diarioEntradaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
