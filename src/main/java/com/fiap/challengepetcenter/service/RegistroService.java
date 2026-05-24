package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.DTO.RegistroRequestDTO;
import com.fiap.challengepetcenter.DTO.RegistroResponseDTO;
import com.fiap.challengepetcenter.exception.DiarioEntradaNaoEncontradoException;
import com.fiap.challengepetcenter.exception.RegistroNaoEncontradoException;
import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.model.Registro;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistroService {

    private final RegistroRepository registroRepository;
    private final DiarioEntradaRepository diarioEntradaRepository;

    @Autowired
    public RegistroService(RegistroRepository registroRepository, DiarioEntradaRepository diarioEntradaRepository) {
        this.registroRepository = registroRepository;
        this.diarioEntradaRepository = diarioEntradaRepository;
    }

    @Transactional
    public RegistroResponseDTO salvar(RegistroRequestDTO requestDTO) {
        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(requestDTO.entradaId())
                .orElseThrow(() -> new DiarioEntradaNaoEncontradoException("DiarioEntrada não encontrado com ID: " + requestDTO.entradaId()));

        Registro registro = new Registro();
        registro.setEntrada(diarioEntrada);
        registro.setTipo(requestDTO.tipo());
        registro.setSubtipo(requestDTO.subtipo());
        registro.setValor(requestDTO.valor());
        registro.setUnidade(requestDTO.unidade());
        registro.setNota(requestDTO.nota());

        Registro registroSalvo = registroRepository.save(registro);

        return RegistroResponseDTO.fromEntity(registroSalvo);
    }

    @Transactional(readOnly = true)
    public Page<RegistroResponseDTO> listarTodos(Pageable pageable) {
        Page<Registro> registros = registroRepository.findAll(pageable);

        return registros.map(RegistroResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public RegistroResponseDTO buscarPorId(Long id) {
        Registro registro = registroRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Registro não encontrado com ID: " + id));
        return RegistroResponseDTO.fromEntity(registro);
    }

    @Transactional
    public RegistroResponseDTO atualizar(Long id, RegistroRequestDTO requestDTO) {
        Registro registroExistente = registroRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Registro não encontrado com ID: " + id));

        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(requestDTO.entradaId())
                .orElseThrow(() -> new DiarioEntradaNaoEncontradoException("DiarioEntrada não encontrado com ID: " + requestDTO.entradaId()));

        registroExistente.setEntrada(diarioEntrada);
        registroExistente.setTipo(requestDTO.tipo());
        registroExistente.setSubtipo(requestDTO.subtipo());
        registroExistente.setValor(requestDTO.valor());
        registroExistente.setUnidade(requestDTO.unidade());
        registroExistente.setNota(requestDTO.nota());

        Registro registroAtualizado = registroRepository.save(registroExistente);

        return RegistroResponseDTO.fromEntity(registroAtualizado);

    }


    @Transactional
    public void deletar(Long id) {
        if (!registroRepository.existsById(id)) {
            throw new RuntimeException("Registro não encontrado");
        }
        registroRepository.deleteById(id);
    }
}

