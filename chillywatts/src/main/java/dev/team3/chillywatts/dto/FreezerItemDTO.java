package dev.team3.chillywatts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.team3.chillywatts.enums.EstadoBorracha;
import dev.team3.chillywatts.enums.TecnologiaFreezer;
import dev.team3.chillywatts.enums.TipoFreezer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO que representa um item da lista de freezers no request.
 * Cada freezer tem tipo, tecnologia, estado da borracha e quantidade.
 * Não é entidade JPA — os dados são processados em memória e não são salvos no banco.
 */
public class FreezerItemDTO {

    /** Marca do freezer (opcional — ex: Metalfrio, Gelopar) */
    @JsonProperty("marca")
    private String marca;

    /** Tipo do freezer: EXIBICAO (vitrine) ou ARMAZENAMENTO (depsito) */
    @JsonProperty("tipo")
    @NotNull(message = "tipo é obrigatório")
    private TipoFreezer tipo;

    /** Tecnologia: CONVENCIONAL ou INVERTER (consome até 30% menos) */
    @JsonProperty("tecnologia")
    @NotNull(message = "tecnologia é obrigatória")
    private TecnologiaFreezer tecnologia;

    /** Estado da borracha de vedação: INTEGRA ou GASTA (+25% consumo) */
    @JsonProperty("estado_borracha")
    @NotNull(message = "estado_borracha é obrigatório")
    private EstadoBorracha estadoBorracha;

    /** Quantidade de freezers deste tipo (mínimo 1) */
    @JsonProperty("quantidade")
    @NotNull(message = "quantidade é obrigatória")
    @Min(value = 1, message = "quantidade deve ser pelo menos 1")
    private Integer quantidade;

    /** Consumo mensal em kWh por UNIDADE (potência × 24h × 30d × fatorBorracha) — preenchido pelo backend */
    @JsonProperty("consumo_kwh")
    private Double consumoKwh;

    public FreezerItemDTO() {
    }

    public FreezerItemDTO(String marca, TipoFreezer tipo, TecnologiaFreezer tecnologia, EstadoBorracha estadoBorracha, Integer quantidade) {
        this.marca = marca;
        this.tipo = tipo;
        this.tecnologia = tecnologia;
        this.estadoBorracha = estadoBorracha;
        this.quantidade = quantidade;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public TipoFreezer getTipo() {
        return tipo;
    }

    public void setTipo(TipoFreezer tipo) {
        this.tipo = tipo;
    }

    public TecnologiaFreezer getTecnologia() {
        return tecnologia;
    }

    public void setTecnologia(TecnologiaFreezer tecnologia) {
        this.tecnologia = tecnologia;
    }

    public EstadoBorracha getEstadoBorracha() {
        return estadoBorracha;
    }

    public void setEstadoBorracha(EstadoBorracha estadoBorracha) {
        this.estadoBorracha = estadoBorracha;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getConsumoKwh() {
        return consumoKwh;
    }

    public void setConsumoKwh(Double consumoKwh) {
        this.consumoKwh = consumoKwh;
    }
}
