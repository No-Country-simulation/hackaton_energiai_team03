package dev.team3.chillywatts.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String freezersJson;

    private LocalDateTime atualizadoEm;

    public Inventario (){}

    public Inventario(String cnpj, String freezersJson) {
        this.cnpj = cnpj;
        this.freezersJson = freezersJson;
        this.atualizadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getFreezersJson() {
        return freezersJson;
    }

    public void setFreezersJson(String freezersJson) {
        this.freezersJson = freezersJson;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
