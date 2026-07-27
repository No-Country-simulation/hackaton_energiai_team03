package dev.team3.chillywatts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
    Tecnologia do Freezer, afeta o consumo.
    CONVENCIONAL = resulta em um consumo padrão
    INVERTER = resulta em um consumo abaixo do padrão


*/
public enum TecnologiaFreezer {
    CONVENCIONAL,
    INVERTER;

    /** Converte string do JSON para enum (case-insensitive). Ex: "inverter" → INVERTER */
    @JsonCreator
    public static TecnologiaFreezer fromString(String value) {
        if (value == null) return null;
        return switch (value.toLowerCase()) {
            case "inverter" -> INVERTER;
            case "convencional" -> CONVENCIONAL;
            default -> throw new IllegalArgumentException("Tecnologia inválida: " + value);
        };
    }

    /** Converte enum para string do JSON. Ex: INVERTER → "Inverter" */
    @JsonValue
    public String toJson() {
        return switch (this) {
            case INVERTER -> "Inverter";
            case CONVENCIONAL -> "Convencional";
        };
    }
}
