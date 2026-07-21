package dev.team3.chillywatts.service;

import dev.team3.chillywatts.dto.FreezerItemDTO;
import dev.team3.chillywatts.enums.EpocaAno;
import dev.team3.chillywatts.enums.EstadoBorracha;
import dev.team3.chillywatts.enums.TecnologiaFreezer;
import dev.team3.chillywatts.enums.TipoFreezer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsável por calcular o consumo teórico de energia.
 * Fórmula: potência × 24h × 30d × quantidade × fator_borracha × fator_sazonal
 */
@Service
public class ConsumoService {

    /**
     * Calcula o consumo teórico total de todos os freezers.
     * Para cada freezer: busca a potência pela tabela (tipo × tecnologia),
     * multiplica por 24h × 30d × quantidade, aplica +25% se borracha gasta,
     * e por fim aplica o fator sazonal (verão ×1,20, inverno ×0,80).
     */
    public Double calcularConsumoTeorico(List<FreezerItemDTO> freezers, EpocaAno epocaAno) {
        if (freezers == null || freezers.isEmpty()) return 0.0;

        Double consumoTotal = 0.0;

        for (FreezerItemDTO freezer : freezers) {
            Double consumoUnitario = calcularConsumoUnitario(freezer);
            consumoTotal += consumoUnitario * freezer.getQuantidade();
        }

        // Aplica o multiplicador sazonal o total
        Double fatorSazonal = obterFatorSazonal(epocaAno);
        consumoTotal *= fatorSazonal;

        return consumoTotal;
    }

    /**
     * Preenche o campo consumoKwh de cada freezer com o consumo mensal por unidade (kWh).
     * Inclui fator borracha mas NÃO inclui sazonalidade (que é aplicada no total).
     */
    public void preencherConsumoPorFreezer(List<FreezerItemDTO> freezers) {
        if (freezers == null) return;
        for (FreezerItemDTO freezer : freezers) {
            freezer.setConsumoKwh(calcularConsumoUnitario(freezer));
        }
    }

    /**
     * Calcula o consumo mensal por UNIDADE em kWh (sem sazonalidade).
     * Fórmula: potência × 24h × 30d × fatorBorracha
     */
    private Double calcularConsumoUnitario(FreezerItemDTO freezer) {
        Double potencia = obterPotencia(freezer.getTipo(), freezer.getTecnologia());
        Double consumo = potencia * 24 * 30;
        if (freezer.getEstadoBorracha() == EstadoBorracha.GASTA) {
            consumo *= 1.25;
        }
        return consumo;
    }

    /** Retorna o multiplicador sazonal: verão = 1,20 (mais quente, mais gasto), inverno = 0,80, outras = 1,0. */
    private Double obterFatorSazonal(EpocaAno epocaAno) {
        if (epocaAno == null) return 1.0;
        return switch (epocaAno) {
            case VERAO -> 1.20;
            case INVERNO -> 0.80;
            default -> 1.0;
        };
    }

    /**
     * Retorna a potência em kW com base no tipo e tecnologia do freezer.
     * Tabela de valores:
     *   EXIBICAO + CONVENCIONAL = 0,25 kW
     *   EXIBICAO + INVERTER    = 0,18 kW
     *   ARMAZENAMENTO + CONVENCIONAL = 0,15 kW
     *   ARMAZENAMENTO + INVERTER    = 0,10 kW
     */
    private Double obterPotencia(TipoFreezer tipo, TecnologiaFreezer tecnologia) {
        boolean isExibicao = tipo == TipoFreezer.EXIBICAO;

        if (isExibicao) {
            return tecnologia == TecnologiaFreezer.INVERTER ? 0.18 : 0.25;
        } else {
            return tecnologia == TecnologiaFreezer.INVERTER ? 0.10 : 0.15;
        }
    }

}
