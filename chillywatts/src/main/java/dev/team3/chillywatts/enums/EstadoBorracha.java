package dev.team3.chillywatts.enums;

/**
    Estado das borrachas do Freezer
    INTEGRA: consumo padrão
    GASTA: consumo com uma penalidade de 25% mais caro no cálculo


*/

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoBorracha {
    INTEGRA,
    GASTA;


    /** Converte string do JSON para enum (case-insensitive). Ex: "integra" → INTEGRA */
    @JsonCreator
    public static EstadoBorracha fromString(String value) {
        if (value == null) return null;
        return switch (value.toLowerCase()) {
            case "integra" -> INTEGRA;
            case "gasta" -> GASTA;
            default -> throw new IllegalArgumentException("Estado da borracha inválido: " + value);
        };
    }

    /** Converte enum para string do JSON. Ex: INTEGRA → "Integra" */
    @JsonValue
    public String toJson() {
        return switch (this) {
            case INTEGRA -> "Integra";
            case GASTA -> "Gasta";
        };
    }
}
