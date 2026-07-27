package dev.team3.chillywatts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.team3.chillywatts.enums.EpocaAno;
import dev.team3.chillywatts.enums.HorasAltoConsumo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO de entrada — define o formato do JSON enviado no POST /analise-energetica.
 * Campos obrigatórios: consumo_kwh, uso_horario_pico, horas_alto_consumo, epoca_ano.
 * Campos opcionais: nome, cnpj (preenchidos depois via PATCH pelo chatbot).
 */

public class AnaliseEnergeticaRequestDTO {

    /** Consumo mensal real da sorveteria em kW/h */
    @JsonProperty("consumo_kwh")
    @NotNull(message = "consumo_kwh é obrigatório!")
    @Min(value = 0, message = "consumo_kwh deve ser positivo")
    private Double consumoKwh;

    /** true se a sorveteria fica em região de pico tarifário (geralmente 18h-21h) */
    @JsonProperty("uso_horario_pico")
    @NotNull(message = "uso_horario_pico é obrigatório!")
    private Boolean usoHorarioPico;

    /** Nível de movimento: Baixo, Médio ou Alto */
    @JsonProperty("horas_alto_consumo")
    @NotNull(message = "horas_alto_consumo é obrigatório!")
    private HorasAltoConsumo horasAltoConsumo;

    /** Época do ano (afeta o fator sazonal do consumo */
    @JsonProperty("epoca_ano")
    @NotNull(message = "época do ano é obrigatória!")
    private EpocaAno epocaAno;

    /** Lista de freezers da sorveteria (tipo, tecnologia, borracha, quantidade) */
    @JsonProperty("freezers")
    @Valid
    private List<FreezerItemDTO> freezers;

    /** Nome do responsável (opcional, preenchido deppois pelo chatbot) */
    @JsonProperty("nome")
    private String nome;

    /** CNPJ da sorveteria (opcional, preenchido deppois pelo chatbot) */
    @JsonProperty("cnpj")
    private String cnpj;

    /** ID do inventário vinculado (opcional — linka a análise ao inventário salvo) */
    @JsonProperty("inventario_id")
    private Long inventarioId;

    /** Se true, salva a análise no histórico. Se false, retorna o resultado sem gravar (simulação). */
    @JsonProperty("salvar")
    private Boolean salvar = true;

    public AnaliseEnergeticaRequestDTO() {}

    public AnaliseEnergeticaRequestDTO(Double consumoKwh, Boolean usoHorarioPico, HorasAltoConsumo horasAltoConsumo,
                                       EpocaAno epocaAno, List<FreezerItemDTO> freezers) {
        this.consumoKwh = consumoKwh;
        this.usoHorarioPico = usoHorarioPico;
        this.horasAltoConsumo = horasAltoConsumo;
        this.epocaAno = epocaAno;
        this.freezers = freezers;
    }

    public Double getConsumoKwh() {
        return consumoKwh;
    }

    public void setConsumoKwh(Double consumoKwh) {
        this.consumoKwh = consumoKwh;
    }

    public Boolean getUsoHorarioPico() {
        return usoHorarioPico;
    }

    public void setUsoHorarioPico(Boolean usoHorarioPico) {
        this.usoHorarioPico = usoHorarioPico;
    }

    public HorasAltoConsumo getHorasAltoConsumo() {
        return horasAltoConsumo;
    }

    public void setHorasAltoConsumo(HorasAltoConsumo horasAltoConsumo) {
        this.horasAltoConsumo = horasAltoConsumo;
    }

    public EpocaAno getEpocaAno() {
        return epocaAno;
    }

    public void setEpocaAno(EpocaAno epocaAno) {
        this.epocaAno = epocaAno;
    }

    public List<FreezerItemDTO> getFreezers() {
        return freezers;
    }

    public void setFreezers(List<FreezerItemDTO> freezers) {
        this.freezers = freezers;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Long getInventarioId() {
        return inventarioId;
    }

    public void setInventarioId(Long inventarioId) {
        this.inventarioId = inventarioId;
    }

    public Boolean getSalvar() {
        return salvar;
    }

    public void setSalvar(Boolean salvar) {
        this.salvar = salvar;
    }
}
