package dev.team3.chillywatts.controller;

import dev.team3.chillywatts.entity.Inventario;
import dev.team3.chillywatts.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Controller do inventário de freezers.
 * Permite buscar e atualizar o setup de freezers associado a um CNPJ.
 */
@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Inventário", description = "Gerenciamento do setup de freezers por CNPJ")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    /**
     * GET /api/inventario?cnpj=... — Busca inventário pelo CNPJ.
     * Retorna o freezers_json salvo ou 404 se não existe.
     */
    @Operation(summary = "Buscar inventário por CNPJ", description = "Retorna o setup de freezers associado ao CNPJ")
    @GetMapping("/api/inventario")
    public ResponseEntity<Map<String, Object>> buscarPorCnpj(@RequestParam String cnpj) {
        Optional<Inventario> inv = inventarioService.buscarPorCnpj(cnpj);
        if (inv.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Inventario i = inv.get();
        return ResponseEntity.ok(Map.of(
                "id", i.getId(),
                "cnpj", i.getCnpj(),
                "freezers_json", i.getFreezersJson(),
                "atualizado_em", i.getAtualizadoEm().toString()
        ));
    }

    /**
     * PUT /api/inventario — Salva ou atualiza inventário (upsert).
     * Se já existe para o CNPJ, atualiza. Se não, cria novo.
     */
    @Operation(summary = "Salvar ou atualizar inventário", description = "Cria ou atualiza o setup de freezers de uma sorveteria")
    @PutMapping("/api/inventario")
    public ResponseEntity<Map<String, Object>> salvar(@RequestBody Map<String, String> body) {
        String cnpj = body.get("cnpj");
        String freezersJson = body.get("freezers_json");

        if (cnpj == null || cnpj.isBlank() || freezersJson == null) {
            return ResponseEntity.badRequest().build();
        }

        Inventario inv = inventarioService.salvarOuAtualizar(cnpj.trim(), freezersJson);
        return ResponseEntity.ok(Map.of(
                "id", inv.getId(),
                "cnpj", inv.getCnpj(),
                "freezers_json", inv.getFreezersJson(),
                "atualizado_em", inv.getAtualizadoEm().toString()
        ));
    }
}

