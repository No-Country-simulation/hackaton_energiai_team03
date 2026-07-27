package dev.team3.chillywatts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO que representa uma melhoria acionável para simulação.
 * Cada melhoria tem um identificador, descrição e impacto percentual no custo.
 */

public class MelhoriaSimulacaoDTO {

    /** Identificador único da melhoria (ex: "borracha_0", "tecnologia_1") */
    @JsonProperty("id")
    private String id;

    /** Tipo da melhoria (borracha, tecnologia, porta, consolidar) */
    @JsonProperty("tipo")
    private String tipo;

    /** Nome curto da melhoria */
    @JsonProperty("nome")
    private String nome;

    /** Descrição detalhada do impacto */
    @JsonProperty("desc")
    private String desc;

    /** Ícone para exibição (emoji) */
    @JsonProperty("icon")
    private String icon;

    /** Percentual de redução no custo mensal (ex: 0.08 = 8%) */
    @JsonProperty("impacto")
    private Double impacto;

    /** Economia por individual por freezer (null quando não aplicável, ex: pico, consolidar) */
    @JsonProperty("impacto_por_freezer")
    private Double impactoPorFreezer;

    /** Impacto máximo permitido para esta melhoria (cap), null quando não aplicável */
    @JsonProperty("impacto_max")
    private Double impactoMax;

    /** Quantidade total de freezers afetados por esta melhoria (para seletor 1..N) */
    @JsonProperty("qtd_afetada")
    private Integer qtdAfetada;

    public MelhoriaSimulacaoDTO() {
    }

    public MelhoriaSimulacaoDTO(String id, String tipo, String nome, String desc, String icon, Double impacto) {
        this(id, tipo, nome, desc, icon, impacto, null, null, null);
    }

    public MelhoriaSimulacaoDTO(String id, String tipo, String nome, String desc, String icon, Double impacto, Double impactoPorFreezer) {
        this(id, tipo, nome, desc, icon, impacto, impactoPorFreezer, null, null);
    }

    public MelhoriaSimulacaoDTO(String id, String tipo, String nome, String desc, String icon, Double impacto, Double impactoPorFreezer, Double impactoMax, Integer qtdAfetada) {
        this.id = id;
        this.tipo = tipo;
        this.nome = nome;
        this.desc = desc;
        this.icon = icon;
        this.impacto = impacto;
        this.impactoPorFreezer = impactoPorFreezer;
        this.impactoMax = impactoMax;
        this.qtdAfetada = qtdAfetada;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getImpacto() {
        return impacto;
    }

    public void setImpacto(Double impacto) {
        this.impacto = impacto;
    }

    public Double getImpactoPorFreezer() {
        return impactoPorFreezer;
    }

    public void setImpactoPorFreezer(Double impactoPorFreezer) {
        this.impactoPorFreezer = impactoPorFreezer;
    }

    public Double getImpactoMax() {
        return impactoMax;
    }

    public void setImpactoMax(Double impactoMax) {
        this.impactoMax = impactoMax;
    }

    public Integer getQtdAfetada() {
        return qtdAfetada;
    }

    public void setQtdAfetada(Integer qtdAfetada) {
        this.qtdAfetada = qtdAfetada;
    }

}
