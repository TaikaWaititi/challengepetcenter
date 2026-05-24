package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.DTO.PetRequestDTO;
import com.fiap.challengepetcenter.DTO.PetResponseDTO;
import com.fiap.challengepetcenter.service.PetService;
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
@RequestMapping("/api/pets")
@Tag(name = "Pets", description = "Endpoints para gerenciamento de pets")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    @Operation(
            summary = "Criar pet",
            description = "Cria um novo pet associado a um usuário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pet criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<PetResponseDTO> criar(@Valid @RequestBody PetRequestDTO requestDTO) {
        PetResponseDTO novoPet = petService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPet);
    }

    @GetMapping
    @Operation(
            summary = "Listar pets",
            description = "Retorna uma lista completa de todos os pets cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de pets retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponseDTO.class)
            )
    )
    public ResponseEntity<Page<PetResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<PetResponseDTO> pets = petService.listarTodos(pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar pet por ID",
            description = "Retorna um pet específico baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pet encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet não encontrado"
            )
    })
    public ResponseEntity<PetResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarPorId(id));
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
    public ResponseEntity<Page<PetResponseDTO>> buscarPorUserId(
            @PathVariable Long userId,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<PetResponseDTO> pets = petService.buscarPorUserId(userId, pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/nome/{nome}")
    @Operation(
            summary = "Buscar pets por nome",
            description = "Retorna todos os pets com o nome informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pets encontrados com sucesso"
            )
    })

    public ResponseEntity<Page<PetResponseDTO>> buscarPorNome(
            @PathVariable String nome,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<PetResponseDTO> pets = petService.buscarPorNome(nome, pageable);
        return ResponseEntity.ok(pets);
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
    public ResponseEntity<PetResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PetRequestDTO requestDTO) {
        PetResponseDTO petAtualizado = petService.atualizar(id, requestDTO);
        return ResponseEntity.ok(petAtualizado);
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
        petService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
