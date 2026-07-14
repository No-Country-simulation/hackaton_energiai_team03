package dev.team3.chillywatts.freezer;

import jakarta.persistence.*;

@Entity
@Table(name = "freezer")
public class Freezer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marca;
    private String tipo;
    private String tecnologia;
    private String estadoBorracha;
    private Integer quantidade;

    Freezer(){}

    public Freezer(String marca, String tipo, String tecnologia, String estadoBorracha, Integer quantidade) {
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTecnologia() {
        return tecnologia;
    }

    public void setTecnologia(String tecnologia) {
        this.tecnologia = tecnologia;
    }

    public String getEstadoBorracha() {
        return estadoBorracha;
    }

    public void setEstadoBorracha(String estadoBorracha) {
        this.estadoBorracha = estadoBorracha;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Freezer{" +
                "marca='" + marca + '\'' +
                ", tipo='" + tipo + '\'' +
                ", tecnologia='" + tecnologia + '\'' +
                ", estadoBorracha='" + estadoBorracha + '\'' +
                ", quantidade=" + quantidade +
                '}';
    }


    public double obterPotencia() {
        double potencia = (double)0.0F;
        if (this.tipo.equals("armazenamento") && this.tecnologia.equals("inverter")) {
            potencia = 0.1;
        }

        return potencia;
    }

    public double calcularConsumoTeorico(String epocaAno) {
        double potencia = this.obterPotencia();
        double epoca = (double)1.0F;
        if (epocaAno.equals("verao")) {
            epoca = 1.2;
        }

        if (epocaAno.equals("inverno")) {
            epoca = 0.8;
        }

        double borracha = (double)1.0F;
        if (this.estadoBorracha.equals("gasta")) {
            borracha = (double)1.25F;
        }

        return this.quantidade > 1 ? potencia * (double)720.0F * epoca * borracha * (double)this.quantidade : potencia * (double)720.0F * epoca * borracha;
    }
}


