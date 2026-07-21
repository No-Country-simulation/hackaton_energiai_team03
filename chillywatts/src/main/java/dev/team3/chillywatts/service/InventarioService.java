package dev.team3.chillywatts.service;

import dev.team3.chillywatts.entity.Inventario;
import dev.team3.chillywatts.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service responsável pelo inventário de freezers da sorveteria.
 * Permite buscar e atualizar o setup de freezers associado a um CNPJ.
 */
@Service
public class InventarioService {

    Autowired
    private InventarioRepository inventarioRepository;

    /** Busca inventário pelo CNPJ. Retorna vazio se não existe. */
    public Optional<Inventario> buscarPorCnpj(String cnpj) { return inventarioRepository.findByCnpj(cnpj);}

    /**
     * Salva ou atualiza o inventário de um CNPJ (upsert).
     * Se já existe inventário para esse CNPJ, atualiza o freezers_json.
     * Se não existe, cria um novo.
     */
    public Inventario salvarOuAtualizar(String cnpj, String freezersJson) {
        Optional<Inventario> existente = inventarioRepository.findByCnpj(cnpj);

        if (existente.isPresent()) {
            Inventario inv = existente.get();
            inv.setFreezersJson(freezersJson);
            inv.setAtualizadoEm(LocalDateTime.now());
            return inventarioRepository.save(inv);
        } else {
            Inventario inv = new Inventario(cnpj, freezersJson);
            return inventarioRepository.save(inv);
        }
    }
}
