package dev.team3.chillywatts.service;

import dev.team3.chillywatts.dto.AnaliseEnergeticaRequestDTO;
import dev.team3.chillywatts.dto.FreezerItemDTO;
import dev.team3.chillywatts.dto.MelhoriaSimulacaoDTO;
import dev.team3.chillywatts.enums.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsável por gerar recomendações personalizadas de economia energética.
 * As dicas são baseadas no perfil classificado, configuração dos freezers, consumo
 * e época do ano.
 */
@Service
public class RecomendacaoService {

    /**
     * Gera uma lista de recomendações baseada em múltiplos critérios:
     * 1. Perfil energético (Ineficiente/Moderado/Eficiente)
     * 2. Pico de horário ativo
     * 3. Nível de movimento (alto/médio/baixo)
     * 4. Quantidade de equipamentos
     * 5. Consumo acima do esperado
     * 6. Borracha de vedação gasta (manutenção)
     * 7. Proporção de convencionais vs Inverter
     * 8. Época do ano (inverno/verão)
     */
    public List<String> gerarRecomendacoesEnergetica(AnaliseEnergeticaRequestDTO request, CategoriaEnergetica categoria, Double consumoTeorico) {
        List<String> recomendacoes = new ArrayList<>();

        // 1. dica baseada no perfil classificado
        if (categoria == CategoriaEnergetica.INEFICIENTE) {
            recomendacoes.add("ATENÇÃO: Seu perfil é ineficiente. Ação urgente necessária para reduzir custos na sorveteria.");
        }else if (categoria == CategoriaEnergetica.MODERADO) {
            recomendacoes.add("Seu perfil é Moderado. Pequenas melhorias operacionais podem gerar economia significativa.");
        } else {
            recomendacoes.add("Parabéns! Seu perfil é Eficiente. Mantenha as boas práticas de gestão energética.");
        }

        // 2. Dica sobre pico tarifário
        if (Boolean.TRUE.equals(request.getUsoHorarioPico())) {
            recomendacoes.add("Abasteça os freezers de exibição antes das 18h para evitar a abertura de portas no horário de pico tarifário.");
        }

        // 3. Dica sobre nível de movimento
        if (request.getHorasAltoConsumo() == HorasAltoConsumo.ALTO) {
            recomendacoes.add("Reduza a abertura dos freezers. Movimento intenso aumenta significativamente o consumo. Mantenha as portas fechadas sempre que possível.");
        } else if (request.getHorasAltoConsumo() == HorasAltoConsumo.MEDIO) {
            recomendacoes.add("Seu nível de movimento é moderado. Verifique se é possível reduzir a abertura dos freezers fora do horário de atendimento.");
        }

        // 4. Dica sobre quantidade de equipamentos
        int totalEquipamentos = request.getFreezers().stream()
                .mapToInt(FreezerItemDTO::getQuantidade).sum();

        if (totalEquipamentos > 6 ) {
            recomendacoes.add("Consolide os produtos em menos freezers para reduzir o consumo.");
        }

        // 5. Dica se consumo real está acima do teórico
        if (consumoTeorico > 0 && request.getConsumoKwh() > consumoTeorico * 1.15) {
            recomendacoes.add("Seu consumo real está acima do esperado. Verifique freezers com borracha de vedação gasta.");
        }

        // 6. Dicas de manutenção por freezer com borracha gasta
        if (request.getFreezers() != null) {
            for (FreezerItemDTO freezer : request.getFreezers()) {
                if (freezer.getEstadoBorracha() == EstadoBorracha.GASTA) {
                    String marca = freezer.getMarca() != null ? freezer.getMarca() : "Desconhecida";
                    recomendacoes.add(String.format("Agende manutenção para trocar a borracha do freezer %s (%s). Borracha gasta aumenta o consumo em 25%%.",
                            freezer.getTecnologia().toJson(), marca));
                }
            }

            // 7. Dica para substituir modelos convencionais por Inverter
            long convencionais = request.getFreezers().stream()
                    .filter(f -> f.getTecnologia() == TecnologiaFreezer.CONVENCIONAL)
                    .mapToInt(FreezerItemDTO::getQuantidade).sum();
            if (convencionais > totalEquipamentos / 2) {
                recomendacoes.add("Considere substituir freezers Convencionais por Inverter. Equipamentos Inverter consomem até 30% menos energia.");
            }
        }

        // 8. Dicas sazonais
        if (request.getEpocaAno() == EpocaAno.INVERNO) {
            recomendacoes.add("Época de inverno: consolide os produtos em menos freezers para reduzir o consumo.");
        } else if (request.getEpocaAno() == EpocaAno.VERAO) {
            recomendacoes.add("Época de verão: movimento alto é esperado. Foque em manter portas fechadas e borrachas íntegras.");
        }

        // Dica sempre presente
        recomendacoes.add("Mantenha os freezers de exibição sempre fechados quando não estiver atendendo clientes.");

        return recomendacoes;

    }

    /**
     * Gera a lista de melhorias acionáveis para simulação.
     * Cada melhoria tem um impacto percentual baseado em dados do setor.
     */
    /**
     * Gera melhorias com impacto real baseado no consumo efetivo dos freezers.
     *
     * @param request        dados da análise (freezers, uso de pico, etc.)
     * @param consumoTotalKwh consumo total com sazonalidade (kWh/mês) — usado como base para calcular o impacto %
     */
    public List<MelhoriaSimulacaoDTO> gerarMelhorias(AnaliseEnergeticaRequestDTO request, Double consumoTotalKwh) {
        List<MelhoriaSimulacaoDTO> melhorias = new ArrayList<>();
        List<FreezerItemDTO> freezers = request.getFreezers();
        int idContador = 0;

        if (consumoTotalKwh == null || consumoTotalKwh == 0) return melhorias;

        // ── Borracha gasta ──
        // Corrigir borracha remove o +25% de penalidade daquele freezer.
        // Economia por freezer: potencia × 24 × 30 × 0.25
        double economiaBorrachaTotal = 0;
        int qtdBorrachaGasta = 0;

        for (FreezerItemDTO f : freezers) {
            if (f.getEstadoBorracha() == EstadoBorracha.GASTA) {
                double potencia = obterPotencia(f.getTipo(), f.getTecnologia());
                double economiaPorUnidade = potencia * 24 * 30 * 0.25;
                economiaBorrachaTotal += economiaPorUnidade * f.getQuantidade();
                qtdBorrachaGasta += f.getQuantidade();
            }
        }

        if (qtdBorrachaGasta > 0) {
            double impactoTotal = economiaBorrachaTotal / consumoTotalKwh;
            double impactoPorUnidade = economiaBorrachaTotal / qtdBorrachaGasta / consumoTotalKwh;
            melhorias.add(new MelhoriaSimulacaoDTO(
                    "melhoria_" + (idContador++),
                    "borracha",
                    "Trocar borracha de vedação",
                    qtdBorrachaGasta + " freezer(s) com borracha gasta",
                    "\uD83D\uDD27",
                    impactoTotal,
                    impactoPorUnidade,
                    impactoTotal,
                    qtdBorrachaGasta
            ));
        }

        // ── Convencional → Inverter ──
        // Economia por freezer: (potenciaConv - potenciaInv) × 24 × 30
        double economiaInverterTotal = 0;
        int qtdConvencionais = 0;

        for (FreezerItemDTO f : freezers) {
            if (f.getTecnologia() == TecnologiaFreezer.CONVENCIONAL) {
                double potenciaConv = obterPotencia(f.getTipo(), TecnologiaFreezer.CONVENCIONAL);
                double potenciaInv = obterPotencia(f.getTipo(), TecnologiaFreezer.INVERTER);
                double economiaPorUnidade = (potenciaConv - potenciaInv) * 24 * 30;
                economiaInverterTotal += economiaPorUnidade * f.getQuantidade();
                qtdConvencionais += f.getQuantidade();
            }
        }

        if (qtdConvencionais > 0) {
            double impactoTotal = economiaInverterTotal / consumoTotalKwh;
            double impactoPorUnidade = economiaInverterTotal / qtdConvencionais / consumoTotalKwh;
            melhorias.add(new MelhoriaSimulacaoDTO(
                    "melhoria_" + (idContador++),
                    "tecnologia",
                    "Migrar para Inverter",
                    qtdConvencionais + " freezer(es) convencional(is)",
                    "⚡",
                    impactoTotal,
                    impactoPorUnidade,
                    impactoTotal,
                    qtdConvencionais
            ));
        }

        // ── Pico tarifário: 5% fixo (mudança de comportamento) ──
        if (Boolean.TRUE.equals(request.getUsoHorarioPico())) {
            melhorias.add(new MelhoriaSimulacaoDTO(
                    "melhoria_" + (idContador++),
                    "porta",
                    "Reduzir abertura no pico",
                    "Evite abertura de portas entre 18h-21h — reduz perda de calor",
                    "\uD83D\uDEAA",
                    0.05
            ));
        }

        // ── Consolidação: 5% fixo (redução operacional) ──
        int totalEquipamentos = freezers.stream()
                .mapToInt(FreezerItemDTO::getQuantidade)
                .sum();

        if (totalEquipamentos > 3) {
            melhorias.add(new MelhoriaSimulacaoDTO(
                    "melhoria_" + (idContador++),
                    "consolidar",
                    "Consolidar em menos freezers",
                    totalEquipamentos + " freezers — junte produtos em menos unidades para reduzir consumo",
                    "\uD83D\uDCE6",
                    0.05
            ));
        }

        return melhorias;
    }

    /** Busca potência (kw) pela tabela tipo * tecnologia. */
    private double obterPotencia(TipoFreezer tipo, TecnologiaFreezer tecnologia) {
        boolean isExibicao = tipo == TipoFreezer.EXIBICAO;
        if (isExibicao) {
            return tecnologia == TecnologiaFreezer.INVERTER ? 0.18 : 0.25;
        } else {
            return tecnologia == TecnologiaFreezer.INVERTER ? 0.10 : 0.15;
        }
    }


}
