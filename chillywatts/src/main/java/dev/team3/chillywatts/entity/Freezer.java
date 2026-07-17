package dev.team3.chillywatts.entity;

import dev.team3.chillywatts.enums.EstadoBorracha;
import dev.team3.chillywatts.enums.TecnologiaFreezer;
import dev.team3.chillywatts.enums.TipoFreezer;
import jakarta.persistence.*;

/*

    Entidade referente a um freezer, contendo seus atributos importantes para análise de consumo
    Além da marca que serve mais como um identificador pro usuário diferenciar os freezer.


*/

@Entity
@Table(name = "freezer")
public class Freezer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marca;

    @Enumerated(EnumType.STRING)
    private TipoFreezer tipo;
    @Enumerated(EnumType.STRING)
    private TecnologiaFreezer tecnologia;
    @Enumerated(EnumType.STRING)
    private EstadoBorracha estadoBorracha;
    private Integer quantidade;

    Freezer(){}

    public Freezer(String marca, TipoFreezer tipo, TecnologiaFreezer tecnologia, EstadoBorracha estadoBorracha, Integer quantidade) {
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


    //Função que retorna o valor de potência baseado no tipo e tecnologia do freezer.
    public double obterPotencia() {
        double potencia = (double)0.0F;
        if (this.tipo.equals("armazenamento") && this.tecnologia.equals("inverter")) {
            potencia = 0.1;
        }
        if (this.tipo.equals("armazenamento") && this.tecnologia.equals("convencional")) {
            potencia = 0.15;
        }
        if (this.tipo.equals("mostruario") && this.tecnologia.equals("inverter")) {
            potencia = 0.18;
        }
        if (this.tipo.equals("mostruario") && this.tecnologia.equals("convencional")) {
            potencia = 0.25;
        }

        return potencia;
    }

    /*
        Funcão que faz o cálculo de consumo teórico do freezer(s), baseado na potência, época do ano,
        estado da borracha e quantidade.
    */
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


