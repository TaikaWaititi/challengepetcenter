package com.fiap.challengepetcenter.controller;


import com.fiap.challengepetcenter.dto.response.PetVeterinarioResponseDTO;
import com.fiap.challengepetcenter.service.PetVeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet-veterinarios")
@Tag(name = "PetVeterinários", description = "Endpoints para gerenciamento do relacionamento entre pets e veterinários")
public class PetVeterinarioController {

    @Autowired
    private PetVeterinarioService petVeterinarioService;


    @GetMapping
    @Operation(
            summary = "Listar relacionamento entre pets e veterinários",
            description = "Retorna uma lista completa de todos os relacionamento entre pets e veterinários cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de relacionamento entre pets e veterinários retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetVeterinarioResponseDTO.class)
            )
    )
    public ResponseEntity<Page<PetVeterinarioResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<PetVeterinarioResponseDTO> petsVeterinarios = petVeterinarioService.listarTodos(pageable);
        return ResponseEntity.ok(petsVeterinarios);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar relacionamento entre pet e veterinário por ID",
            description = "Retorna um relacionamento entre pet e veterinário específico baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Relacionamento entre pets e veterinários encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Relacionamento entre pet e veterinário não encontrado"
            )
    })
    public ResponseEntity<PetVeterinarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petVeterinarioService.buscarPorId(id));
    }


    @GetMapping("/veterinario/{veterinarioId}")
    @Operation(
            summary = "Buscar relacionamento entre pets e veterinários por veterinário",
            description = "Retorna todos os relacionamento entre pets e veterinários associados a um veterinário específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Relacionamento entre pets e veterinários encontrados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veterinário não encontrado"
            )
    })
    public ResponseEntity<Page<PetVeterinarioResponseDTO>> buscarPorVeterinario(
            @PathVariable Long veterinarioId,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<PetVeterinarioResponseDTO> petsVeterinarios = petVeterinarioService.buscarPorVeterinarioId(veterinarioId, pageable);
        return ResponseEntity.ok(petsVeterinarios);
    }

    @GetMapping("/pet/{petId}")
    @Operation(
            summary = "Buscar relacionamento entre pets e veterinários por pet",
            description = "Retorna todos os relacionamento entre pets e veterinários associados a um pet específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Relacionamento entre pets e veterinários encontrados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet não encontrado"
            )
    })
    public ResponseEntity<Page<PetVeterinarioResponseDTO>> buscarPorPet(
            @PathVariable Long petId,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<PetVeterinarioResponseDTO> petsVeterinarios = petVeterinarioService.buscarPorPetId(petId, pageable);
        return ResponseEntity.ok(petsVeterinarios);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar pet",
            description = "Remove um relacionamento entre pet e veterinário do sistema baseado no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Relacionamento entre pet e veterinário removido com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Relacionamento entre pet e veterinário  não encontrado"
            )
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        petVeterinarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
