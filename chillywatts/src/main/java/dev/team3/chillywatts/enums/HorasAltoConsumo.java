package dev.team3.chillywatts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HorasAltoConsumo {
    BAIXO,
    MEDIO,
    ALTO;

    /** Converte string do JSON para enum (aceita com ou sem acento). Ex: "Médio → MEDIO */
    @JsonCreator
    public static HorasAltoConsumo fromString(String value) {
        if (value == null) return null;
        return switch (value.toLowerCase().replace("é", "e")){
            case "baixo" -> BAIXO;
            case "medio" -> MEDIO;
            case "alto" -> ALTO;
            default -> throw new IllegalArgumentException("Nível de consumo inválido: " + value);
        };
    }

    /** Converte enum para string do JSON com acento. Ex: MEDIO → "Médio" */
    @JsonValue
    public String toJson() {
        return switch (this) {
            case BAIXO -> "Baixo";
            case MEDIO -> "Médio";
            case ALTO -> "Alto";
        };
    }
}
