package dev.team3.chillywatts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Categoria energética do perfil da sorveteria (resultado da classificação).
 * EFICIENTE: consumo dentro do esperado, boas práticas
 * MODERADO: algumas melhorias possíveis
 * INEFICIENTE: ação urgente necessária para reduzir custos
 *
 * JSON: "Eficiente", "Moderado", "Ineficiente" (primeira letra maiúscula)
 * DB: armazenado como VARCHAR (EFICIENTE, MODERADO, INEFICIENTE)
 *
 * Pode vir do ML Python (Random Forest) ou do fallback Java (regra de pontos).
 */
public enum CategoriaEnergetica {
    EFICIENTE,
    MODERADO,
    INEFICIENTE;

    /** Converte string do Python ou JSON para enum. Aceita "Eficiente" ou "EFICIENTE". */
    @JsonCreator
    public static CategoriaEnergetica fromString(String value) {
        if (value == null) return null;
        return switch (value) {
            case "Eficiente", "EFICIENTE" -> EFICIENTE;
            case "Moderado", "MODERADO" -> MODERADO;
            case "Ineficiente", "INEFICIENTE" -> INEFICIENTE;
            default -> throw new IllegalArgumentException("Categoria inválida: " + value);
        };
    }

    /** Converte enum para string do JSON. Ex: MODERADO → "Moderado" */
    @JsonValue
    public String toJson() {
        return switch (this) {
            case EFICIENTE -> "Eficiente";
            case MODERADO -> "Moderado";
            case INEFICIENTE -> "Ineficiente";
        };
    }
}
