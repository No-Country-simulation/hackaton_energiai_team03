package dev.team3.chillywatts.entity;

import dev.team3.chillywatts.enums.CategoriaEnergetica;
import dev.team3.chillywatts.enums.EpocaAno;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity JPA — mapeia a tabela "analise_historico" no banco de dados.
 * Armazena o resultado completo de cada análise energética realizada.
 * Os enums EpocaAno e CategoriaEnergetica são salvos como VARCHAR (@Enumerated.STRING).
 */
@Entity
@Table(name = "analise_historico")
public class AnaliseHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;           // Nome do responsável (preenchido depois via PATCH)
    private String cnpj;           // CNPJ da sorveteria (preenchido depois via PATCH)
    private Double consumoRealKwh; // Consumo real informado pelo usuário

    @Enumerated(EnumType.STRING)
    private EpocaAno epocaAno;     // Época do ano (afeta fator sazonal)

    private String usoHorarioPico; // "Alto" ou "Baixo" (mapeado de true/false)

    @Enumerated(EnumType.STRING)
    private CategoriaEnergetica perfilEnergetico; // Eficiente, Moderado ou Ineficiente

    private Double probabilidade;               // Confiança da classificação (0 a 1)
    private Double consumoTeoricoEstimadokwh;   // Consumo calculado pelo backend
    private Double custoMensalAtual;            // Custo em R$ (consumo × 0,75)
    private Double economiaEstimadaPotencial;   // Economia potencial em R$

    @Column(columnDefinition = "TEXT")
    private String freezersJson;                // Lista de freezers em JSON (para reutilização/simulação)
    private Long inventarioId;                  // ID do inventário usado (NULL se não usou)
    private LocalDateTime dataAnalise;          // Data/hora da análise (auto preenchido)

    /** Construtor padrão — define automaticamente a data/hora da análise. */
    public AnaliseHistorico() {
        this.dataAnalise = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getConsumoRealKwh() {
        return consumoRealKwh;
    }

    public void setConsumoRealKwh(Double consumoRealKwh) {
        this.consumoRealKwh = consumoRealKwh;
    }

    public EpocaAno getEpocaAno() {
        return epocaAno;
    }

    public void setEpocaAno(EpocaAno epocaAno) {
        this.epocaAno = epocaAno;
    }

    public String getUsoHorarioPico() {
        return usoHorarioPico;
    }

    public void setUsoHorarioPico(String usoHorarioPico) {
        this.usoHorarioPico = usoHorarioPico;
    }

    public CategoriaEnergetica getPerfilEnergetico() {
        return perfilEnergetico;
    }

    public void setPerfilEnergetico(CategoriaEnergetica perfilEnergetico) {
        this.perfilEnergetico = perfilEnergetico;
    }

    public Double getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(Double probabilidade) {
        this.probabilidade = probabilidade;
    }

    public Double getConsumoTeoricoEstimadokwh() {
        return consumoTeoricoEstimadokwh;
    }

    public void setConsumoTeoricoEstimadokwh(Double consumoTeoricoEstimadokwh) {
        this.consumoTeoricoEstimadokwh = consumoTeoricoEstimadokwh;
    }

    public Double getCustoMensalAtual() {
        return custoMensalAtual;
    }

    public void setCustoMensalAtual(Double custoMensalAtual) {
        this.custoMensalAtual = custoMensalAtual;
    }

    public Double getEconomiaEstimadaPotencial() {
        return economiaEstimadaPotencial;
    }

    public void setEconomiaEstimadaPotencial(Double economiaEstimadaPotencial) {
        this.economiaEstimadaPotencial = economiaEstimadaPotencial;
    }

    public String getFreezersJson() {
        return freezersJson;
    }

    public void setFreezersJson(String freezersJson) {
        this.freezersJson = freezersJson;
    }

    public Long getInventarioId() {
        return inventarioId;
    }

    public void setInventarioId(Long inventarioId) {
        this.inventarioId = inventarioId;
    }

    public LocalDateTime getDataAnalise() {
        return dataAnalise;
    }

    public void setDataAnalise(LocalDateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }
}
