package dev.team3.chillywatts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
    Época do ano — afeta o fator sazonal do consumo teórico.
    VERAO: ×1,20 (mais quente, freezers trabalham mais)
    INVERNO: ×0,80 (mais frio, menos esforço dos compressores)
    PRIMAVERA/OUTONO: ×1,0 (neutro)

 */
public enum EpocaAno {
    VERAO,
    INVERNO,
    PRIMAVERA,
    OUTONO;

    /** Converte string do JSON para enum (aceita com ou sem acento). Ex: "Verão" → VERAO */
    @JsonCreator
    public static EpocaAno fromString(String value) {
        if (value == null) return null;
        return switch (value.toLowerCase().replace("ã", "a")) {
            case "verao" -> VERAO;
            case "inverno" -> INVERNO;
            case "primavera" -> PRIMAVERA;
            case "outono" -> OUTONO;
            default -> throw new IllegalArgumentException("Época inválida: " + value);
        };
    }

    /** Converte enum para string do JSON com acento. Ex: VERAO → "Verão" */
    @JsonValue
    public String toJson() {
        return switch (this) {
            case VERAO -> "Verão";
            case INVERNO -> "Inverno";
            case PRIMAVERA -> "Primavera";
            case OUTONO -> "Outono";
        };
    }
}
