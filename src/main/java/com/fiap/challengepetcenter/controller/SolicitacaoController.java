package com.fiap.challengepetcenter.controller;


import com.fiap.challengepetcenter.dto.request.SolicitacaoRequestDTO;
import com.fiap.challengepetcenter.dto.response.SolicitacaoResponseDTO;
import com.fiap.challengepetcenter.service.SolicitacaoService;
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
@RequestMapping("/api/solicitacoes")
@Tag(name = "Solicitações", description = "Endpoints para gerenciamento de solicitações")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @PostMapping
    @Operation(
            summary = "Criar solicitação",
            description = "Cria um novo solicitação associado a um usuário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Solicitação criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<SolicitacaoResponseDTO> criar(@Valid @RequestBody SolicitacaoRequestDTO requestDTO) {
        SolicitacaoResponseDTO novaSolicitacao = solicitacaoService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaSolicitacao);
    }

    @GetMapping
    @Operation(
            summary = "Listar pets",
            description = "Retorna uma lista completa de todos os pets cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de pets retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponseDTO.class)
            )
    )
    public ResponseEntity<Page<SolicitacaoResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<SolicitacaoResponseDTO> solicitacoes = solicitacaoService.listarTodos(pageable);
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar solicitação por ID",
            description = "Retorna uma solicitação específico baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitação encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Solicitação não encontrado"
            )
    })
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.buscarPorId(id));
    }

    @GetMapping("/veterinario/{veterinarioId}")
    @Operation(
            summary = "Buscar solicitações por usuário",
            description = "Retorna todos os solicitações associados a um usuário específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitações encontrados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public ResponseEntity<Page<SolicitacaoResponseDTO>> buscarPorVeterinarioId(
            @PathVariable Long veterinarioId,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<SolicitacaoResponseDTO> solicitacoes = solicitacaoService.buscarPorVeterinarioId(veterinarioId, pageable);
        return ResponseEntity.ok(solicitacoes);
    }


    @GetMapping("/pet/{petId}")
    @Operation(
            summary = "Buscar solicitações por pet",
            description = "Retorna todos os pets associados a um pet específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "solicitações encontrados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "pet não encontrado"
            )
    })
    public ResponseEntity<Page<SolicitacaoResponseDTO>> buscarPorPetId(
            @PathVariable Long petId,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<SolicitacaoResponseDTO> solicitacoes = solicitacaoService.buscarPorPetId(petId, pageable);
        return ResponseEntity.ok(solicitacoes);
    }


    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Buscar solicitações por usuário",
            description = "Retorna todos os solicitações associados a um usuário específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "solicitações encontrados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public ResponseEntity<Page<SolicitacaoResponseDTO>> buscarPorUserId(
            @PathVariable Long userId,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<SolicitacaoResponseDTO> solicitacoes = solicitacaoService.buscarPorUserId(userId, pageable);
        return ResponseEntity.ok(solicitacoes);
    }

    @PatchMapping("/{id}/aceitar")
    @Operation(
            summary = "Aceitar solicitação",
            description = "Aceita uma solicitação de atendimento veterinário e cria o vínculo entre o pet e o veterinário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitação aceita com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Solicitação não encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitação já foi respondida"
            )
    })
    public ResponseEntity<SolicitacaoResponseDTO> aceitar(@PathVariable Long id) {
        SolicitacaoResponseDTO solicitacao = solicitacaoService.aceitar(id);
        return ResponseEntity.ok(solicitacao);
    }

    @PatchMapping("/{id}/recusar")
    @Operation(
            summary = "Recusar solicitação",
            description = "Recusa uma solicitação de atendimento veterinário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitação recusada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Solicitação não encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitação já foi respondida"
            )
    })
    public ResponseEntity<SolicitacaoResponseDTO> recusar(@PathVariable Long id) {
        SolicitacaoResponseDTO solicitacao = solicitacaoService.recusar(id);
        return ResponseEntity.ok(solicitacao);
    }
}
