package dev.team3.chillywatts.controller;

import dev.team3.chillywatts.dto.AnaliseEnergeticaRequestDTO;
import dev.team3.chillywatts.dto.AnaliseEnergeticaResponseDTO;
import dev.team3.chillywatts.entity.AnaliseHistorico;
import dev.team3.chillywatts.service.AnaliseEnergeticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller principal da API.
 * Recebe as requisições HTTP, valida os dados de entrada (via @Valid) e delega
 * toda a lógica de negócio para o {@link AnaliseEnergeticaService}.
 */
@RestController
@CrossOrigin(origins = "*") // Permite chamadas do frontend (HTML/JS) em qualquer origem
@Tag(name = "Análise Energética", description = "API de análise de consumo energético para sorveterias")
public class AnaliseController {

    @Autowired
    private AnaliseEnergeticaService analiseService;

    /**
     * POST /analise-energetica — Endpoint principal.
     * Recebe os dados de consumo da sorveteria (freezers, pico, época do ano etc.),
     * processa a análise (ML ou fallback Java) e retorna classificação, custo e recomendações.
     */
    @Operation(summary = "Realizar análise energética", description = "Recebe dados de consumo e retorna classificação, probabilidade, custo estimado e recomendações")
    @PostMapping("/analise-energetica")
    public ResponseEntity<AnaliseEnergeticaResponseDTO> analiseEnergetica(
            @Valid @RequestBody AnaliseEnergeticaRequestDTO request) {
        AnaliseEnergeticaResponseDTO response = analiseService.processarAnaliseEnergetica(request);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/analises/{id} — Atualiza nome e CNPJ de uma análise já salva.
     * Usado pelo chatbot depois que o usuário informa seus dados pessoais.
     */
    @Operation(summary = "Atualizar nome e CNPJ da análise", description = "Atualiza os campos nome e CNPJ de uma análise já salva")
    @PatchMapping("/api/analises/{id}")
    public ResponseEntity<AnaliseHistorico> atualizarNomeCnpj(
            @Parameter(description = "ID da análise") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String nome = body.get("nome");
        String cnpj = body.get("cnpj");
        AnaliseHistorico historico = analiseService.atualizarNomeCnpj(id, nome, cnpj);
        return ResponseEntity.ok(historico);
    }

    /**
     * GET /api/analises/historico/por-cnpj?cnpj=... — Busca histórico por CNPJ.
     * Retorna todas as análises salvas para uma sorveteria específica.
     */
    @Operation(summary = "Buscar histórico por CNPJ", description = "Retorna todas as análises salvas para um CNPJ específico")
    @GetMapping("/api/analises/historico/por-cnpj")
    public ResponseEntity<List<AnaliseHistorico>> buscarHistoricoPorCnpj(
            @Parameter(description = "CNPJ da sorveteria") @RequestParam String cnpj) {
        List<AnaliseHistorico> historico = analiseService.buscarHistoricoPorCnpj(cnpj);
        return ResponseEntity.ok(historico);
    }

    /**
     * GET /api/analises/historico — Lista todas as análises salvas.
     * Retorna o histórico completo sem filtro.
     */
    @Operation(summary = "Listar todas as análises", description = "Retorna todas as análises salvas no histórico")
    @GetMapping("/api/analises/historico")
    public ResponseEntity<List<AnaliseHistorico>> listarTodosHistoricos() {
        List<AnaliseHistorico> historico = analiseService.listarTodosHistoricos();
        return ResponseEntity.ok(historico);
    }
}
