package dev.team3.chillywatts.service;

import dev.team3.chillywatts.dto.AnaliseEnergeticaRequestDTO;
import dev.team3.chillywatts.dto.FreezerItemDTO;
import dev.team3.chillywatts.enums.CategoriaEnergetica;
import dev.team3.chillywatts.enums.EpocaAno;
import dev.team3.chillywatts.enums.EstadoBorracha;
import dev.team3.chillywatts.enums.HorasAltoConsumo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsável pela classificação do perfil energético.
 * Tenta primeiro via ML Python (Random Forest); se falhar, usa fallback com regras em Java.
 */
@Service
public class ClassificacaoService {

    /** URL do microservico Python que roda o modelo de ML */
    private static final String ML_SERVICE_URL = "http://localhost:5000/prever";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Envia as 6 features para o Python via POST e retorna o resultado do ML.
     * Se o Python estiver offline ou retornar erro, retorna null (ativa fallback).
     * Features enviadas: estacao_ano, uso_horario_pico, quantidade_equipamentos,
     * estado_borracha, consumo_teorico_estimado_kwh, consumo_real_kwh.
     */

    public Map<String, Object> chamarServicoML(AnaliseEnergeticaRequestDTO request, Double consumoTeorico) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("estacao_ano", request.getEpocaAno().toJson());
            payload.put("uso_horario_pico", mapearUsoPico(request.getUsoHorarioPico()));
            payload.put("quantidade_equipamentos", request.getFreezers() == null ? 0 :
                    request.getFreezers().stream().mapToInt(FreezerItemDTO::getQuantidade).sum());
            payload.put("estado_borracha", agregarBorracha(request.getFreezers()));
            payload.put("consumo_teorico_estimado_kwh", consumoTeorico);
            payload.put("consumo_real_kwh", request.getConsumoKwh());

            return restTemplate.postForObject(ML_SERVICE_URL, payload, Map.class);
        } catch (Exception e) {
            System.err.println("ML service indisponível, usando regra Java: " + e.getMessage());
            return null;
        }
    }

    /**
     * Classificação por regras (fallback Java).
     * Soma pontos de ineficiência:
     *   +1 se pico ativo
     *   +1 se horas de alto consumo = Alto
     *   +1 se consumo real > teórico × margem (30% no verão, 15% noutras estações)
     *   +1 se mais de 6 equipamentos E pico ativo
     * Resultado: >=3 = INEFICIENTE, >=1 = MODERADO, 0 = EFICIENTE
     */
    public CategoriaEnergetica classificarCategoria(AnaliseEnergeticaRequestDTO request, Double consumoTeorico){
        int pontosIneficiencia = 0;

        //Pico de horário ativo
        if (Boolean.TRUE.equals(request.getUsoHorarioPico())) {
            pontosIneficiencia++;
        }

        // Muitas horas de alto consumo
        if (request.getHorasAltoConsumo() == HorasAltoConsumo.ALTO) {
            pontosIneficiencia++;
        }

        // Consumo real acima do teórico com margem de tolerância
        boolean isVerao = request.getEpocaAno() == EpocaAno.VERAO;
        Double margem = isVerao ? 1.30 : 1.15;
        if (consumoTeorico > 0 && request.getConsumoKwh() > consumoTeorico * margem){
            pontosIneficiencia++;
        }

        // Muitos equipamentos com pico ativo
        int totalEquipamentos = request.getFreezers().stream()
                .mapToInt(FreezerItemDTO::getQuantidade)
                .sum();

        if (totalEquipamentos > 6 && Boolean.TRUE.equals(request.getUsoHorarioPico())){
            pontosIneficiencia++;
        }

        // Classificação final baseada na pontuação
        if (pontosIneficiencia >= 3) {
            return CategoriaEnergetica.INEFICIENTE;
        } else if (pontosIneficiencia >= 1) {
            return CategoriaEnergetica.MODERADO;
        } else {
            return CategoriaEnergetica.EFICIENTE;
        }
    }

    /**
     * Calcula a probabilidade (confiança) da classificação.
     * Baseada no ratio consumo_real / consumo_teorico:
     *  - Ineficiente: base 0,7 + ajuste pelo rario (máx 1,0)
     *  - Moderado: base 0,4 + ajuste (máx 0,7)
     *  - Eficiente: base 0,6 - ajuste (mín 0,1)
     */
    public Double calcularProbabilidadeCategoria(AnaliseEnergeticaRequestDTO request, CategoriaEnergetica categoria, Double consumoTeorico) {
        if (consumoTeorico == 0) return 0.5;

        Double ratio = request.getConsumoKwh() / consumoTeorico;

        return switch (categoria) {
            case INEFICIENTE -> Math.min(0.7 + (ratio - 1.0) * 0.2, 1.0);
            case MODERADO -> Math.min(0.4 + (ratio - 1.0) * 0.15, 0.7);
            default -> Math.max(0.1, 0.6 - (1.0 - ratio) * 0.2);
        };
    }

    /**
     * Agrega o estado de borracha de todos os freezers.
     * Se mais de 30% do total tem borracha Gasta, retorna "Gasta".
     * Senão, retorna "Integra".
     */
    private String agregarBorracha(List<FreezerItemDTO> freezers) {
        if (freezers == null || freezers.isEmpty()) return "Integra";

        int totalQtd = 0;
        int gastaQtd = 0;
        for (FreezerItemDTO f : freezers) {
            int qtd = f.getQuantidade() != null ? f.getQuantidade() : 1;
            totalQtd += qtd;
            if (f.getEstadoBorracha() == EstadoBorracha.GASTA) {
                gastaQtd += qtd;
            }
        }
        if (totalQtd == 0) return "Integra";
        return (gastaQtd > totalQtd * 0.3) ? "Gasta" : "Integra";
    }

    /** Converte o boolean do request para o formato que o ML Python espera: true → "Alto", false → "Baixo". */
    public String mapearUsoPico(Boolean usoHorarioPico) {
        return Boolean.TRUE.equals(usoHorarioPico) ? "Alto" : "Baixo";
    }
}
