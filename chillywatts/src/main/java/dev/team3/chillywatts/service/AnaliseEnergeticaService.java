package dev.team3.chillywatts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.team3.chillywatts.dto.AnaliseEnergeticaRequestDTO;
import dev.team3.chillywatts.dto.AnaliseEnergeticaResponseDTO;
import dev.team3.chillywatts.dto.MelhoriaSimulacaoDTO;
import dev.team3.chillywatts.entity.AnaliseHistorico;
import dev.team3.chillywatts.enums.CategoriaEnergetica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Orquestrador principal — coordena o fluxo de uma análise energética.
 * Não contém lógica de negócio própria, apenas delega para os services:
 * {@link ConsumoService}, {@link ClassificacaoService}, {@link RecomendacaoService}
 * e {@link HistoricoService}.
 */
@Service
public class AnaliseEnergeticaService {

    @Autowired
    private ConsumoService consumoService;

    @Autowired
    private ClsssificacaoService classificacaoService;

    @Autowired
    private RecomendacaoService recomendacaoService;

    @Autowired
    private HistoricoService historicoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fluxo completo de análise:
     * 1. Calcula consumo teórico (potência dos freezers * ffator sazonal)
     * 2. Tenta classificar via ML Python - se falhar, usa fallback Java
     * 3. Gera recomendações personalizadas
     * 4. Salva o resultado no banco
     * 5. Retorna o DTO de resposta
     */
    public AnaliseEnergeticaResponseDTO processarAnaliseEnergetica(AnaliseEnergeticaRequestDTO request) {
        // 1. Calcula quanto os freezers deveriam consumir teoricamente
        Double consumoTeorico = consumoService.calcularConsumoTeorico(request.getFreezers(), request.getEpocaAno());
        Double custoEstimadoMensal = request.getConsumoKwh() * 0.75;

        // preenche consumo por freezer (para response e para cálculo de melhorias)
        consumoService.preencherConsumoPorFreezer(request.getFreezers());

        // 2. Tenta ML Python; se offline, usa regra de pontos no Java
        CategoriaEnergetica categoria;
        Double probabilidade;
        Map<String, Object> mlResult = classificacaoService.chamarServicoML(request, consumoTeorico);

        if (mlResult != null) {
            // ML respondeu com sucesso - usa a classificação do Python
            categoria = CategoriaEnergetica.fromString((String) mlResult.get("categoria"));
            probabilidade = ((Number) mlResult.get("probabilidade")).doubleValue();
        } else {
            // Fallback - classificação por regras definidas em Java
            categoria = classificacaoService.classificarCategoria(request, consumoTeorico);
            probabilidade = classificacaoService.calcularProbabilidadeCategoria(request, categoria, consumoTeorico);
        }

        // 3. Gera dicas baseadas no perfil, consumo e configuração dos freezers
        List<String> recomendacoes = recomendacaoService.gerarRecomendacoesEnergetica(request, categoria, consumoTeorico);

        //4. Gera melhorias acionáveis para simulação (impacto real baseado no consumo)
        List<MelhoriaSimulacaoDTO> melhorias = recomendacaoService.gerarMelhorias(request, consumoTeorico);

        //5. Calcula economia potencial multiplicando os fatores de redução (mesmo cálculo do frontend)
        double fatorReducao = 1.0;
        for (MelhoriaSimulacaoDTO m : melhorias) {
            fatorReducao *= (1.0 - m.getImpacto());
        }
        Double economiaPotencialReais = custoEstimadoMensal * (1.0 - fatorReducao);
        Double economiaPotencialKwh = economiaPotencialReais / 0.75;

        // 6. Salva no histórico apenas se 'salvar' for true (similações não persistem)
        Long idSalvo = null;
        if (Boolean.TRUE.equals(request.getSalvar())) {
            AnaliseHistorico historico = new AnaliseHistorico();
            historico.setNome(request.getNome());
            historico.setCnpj(request.getCnpj());
            historico.setConsumoRealKwh(request.getConsumoKwh());
            historico.setConsumoTeoricoEstimadokwh(consumoTeorico);
            historico.setUsoHorarioPico(classificacaoService.mapearUsoPico(request.getUsoHorarioPico()));
            historico.setPerfilEnergetico(categoria);
            historico.setProbabilidade(probabilidade);
            historico.setCustoMensalAtual(custoEstimadoMensal);
            historico.setEpocaAno(request.getEpocaAno());
            historico.setEconomiaEstimadaPotencial(economiaPotencialReais);
            historico.setInventarioId(request.getInventarioId());
            try {
                historico.setFreezersJson(objectMapper.writeValueAsString(request.getFreezers()));
            } catch (Exception e) {
                historico.setFreezersJson("[]");
            }
            historicoService.salvar(historico);
            idSalvo = historico.getId();
        }

        return new AnaliseEnergeticaResponseDTO(idSalvo, categoria, probabilidade, custoEstimadoMensal,
                economiaPotencialKwh, economiaPotencialReais,
                recomendacoes, request.getFreezers(), melhorias);
    }

    /** Busca análises salvas por CNPJ - delega para HistóricoService. */
    public List<AnaliseHistorico> buscaHistoricoPorCnpj(String cnpj) {
        return historicoService.buscarHistoricoPorCnpj(cnpj);
    }

    /** Atualiza nome e CNPJ de uma análise - delega para HistóricoService. */
    public AnaliseHistorico atualizarNomeCnpj(Long id, String nome, String cnpj) {
        return historicoService.atualizarNomeCnpj(id, nome, cnpj);
    }

    /** lista todas as análises salvas - delega para HistóricoService. */
    public List<AnaliseHistorico> listarTodosHistóricos() { return historicoService.listarTodosHistoricos();}
}
