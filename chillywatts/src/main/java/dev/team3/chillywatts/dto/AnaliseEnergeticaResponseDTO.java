package dev.team3.chillywatts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.team3.chillywatts.enums.CategoriaEnergetica;

import java.util.List;

/**
 * DTO de saída — define o formato do JSON retornado no POST /analise-energetica.
 * Contém o resultado completo da análise: classificação, custo, economia e recomendações.
 */
public class AnaliseEnergeticaResponseDTO {

    /** ID da análise salva no banco */
    @JsonProperty("id")
    private Long id;

    /** Classificação do perfil energético (Eficiente, Moderado ou Ineficiente) */
    @JsonProperty("categoria")
    private CategoriaEnergetica categoria;

    /** Confiança da classificação (0 a 1) — quanto maior, mais certo o ML está */
    @JsonProperty("probabilidade")
    private Double probabilidade;

    /** Custo mensal estimado em R$ (consumo × R$ 0,75/kWh) */
    @JsonProperty("custo_estimado_mensal")
    private Double custoEstimadoMensal;

    /** Economia potencial em kWh (20% do consumo real) */
    @JsonProperty("economia_potencial_kwh")
    private Double economiaPotencialKwh;

    /** Economia potencial em R$ (economia_kwh × R$ 0,75) */
    @JsonProperty("economia_potencial_reais")
    private Double economiaPotencialReais;

    /** Lista de recomendações personalizadas para a sorveteria */
    @JsonProperty("recomendacoes")
    private List<String> recomendacoes;

    /** Lista de freezers utilizados na análise (para reutilização e simulação) */
    @JsonProperty("freezers")
    private List<FreezerItemDTO> freezers;

    /** Lista de melhorias acionáveis com impacto percentual para simulação */
    @JsonProperty("melhorias")
    private List<MelhoriaSimulacaoDTO> melhorias;

    public AnaliseEnergeticaResponseDTO() {
    }

    public AnaliseEnergeticaResponseDTO(Long id, CategoriaEnergetica categoria, Double probabilidade, Double custoEstimadoMensal,
                                        Double economiaPotencialKwh, Double economiaPotencialReais,
                                        List<String> recomendacoes, List<FreezerItemDTO> freezers,
                                        List<MelhoriaSimulacaoDTO> melhorias) {
        this.id = id;
        this.categoria = categoria;
        this.probabilidade = probabilidade;
        this.custoEstimadoMensal = custoEstimadoMensal;
        this.economiaPotencialKwh = economiaPotencialKwh;
        this.economiaPotencialReais = economiaPotencialReais;
        this.recomendacoes = recomendacoes;
        this.freezers = freezers;
        this.melhorias = melhorias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoriaEnergetica getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaEnergetica categoria) {
        this.categoria = categoria;
    }

    public Double getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(Double probabilidade) {
        this.probabilidade = probabilidade;
    }

    public Double getCustoEstimadoMensal() {
        return custoEstimadoMensal;
    }

    public void setCustoEstimadoMensal(Double custoEstimadoMensal) {
        this.custoEstimadoMensal = custoEstimadoMensal;
    }

    public Double getEconomiaPotencialKwh() {
        return economiaPotencialKwh;
    }

    public void setEconomiaPotencialKwh(Double economiaPotencialKwh) {
        this.economiaPotencialKwh = economiaPotencialKwh;
    }

    public Double getEconomiaPotencialReais() {
        return economiaPotencialReais;
    }

    public void setEconomiaPotencialReais(Double economiaPotencialReais) {
        this.economiaPotencialReais = economiaPotencialReais;
    }

    public List<String> getRecomendacoes() {
        return recomendacoes;
    }

    public void setRecomendacoes(List<String> recomendacoes) {
        this.recomendacoes = recomendacoes;
    }

    public List<FreezerItemDTO> getFreezers() {
        return freezers;
    }

    public void setFreezers(List<FreezerItemDTO> freezers) {
        this.freezers = freezers;
    }

    public List<MelhoriaSimulacaoDTO> getMelhorias() {
        return melhorias;
    }

    public void setMelhorias(List<MelhoriaSimulacaoDTO> melhorias) {
        this.melhorias = melhorias;
    }

}
