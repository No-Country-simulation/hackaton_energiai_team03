package dev.team3.chillywatts.freezer;

public class Freezer {

    private String marca;
    private String tipo;
    private String tecnologia;
    private String estadoBorracha;
    private Integer quantidade;

    Freezer(){}

    public Freezer(Integer quantidade, String estadoBorracha, String tecnologia, String tipo, String marca) {
        this.quantidade = quantidade;
        this.estadoBorracha = estadoBorracha;
        this.tecnologia = tecnologia;
        this.tipo = tipo;
        this.marca = marca;
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



}
