package dev.team3.chillywatts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
    Definite o tipo de Freezer:
    MOSTRUARIO = Freezers que ficam em exposição e são constantemente manuseados, resultando em maior potência de consumo
        Potência esperada: 0,25 kw/h para convencional e 0,18 kw/h para inverter
    ARMAZENAMENTO = Freezers que ficam no estoque e são manuseados com menos frequência, menor potência em comparação
        Potência esperada: 0,15 kw/h para convencional e 0,10 kw/h para inverter
*/
public enum TipoFreezer {
    MOSTRUARIO,
    ARMAZENAMENTO;

    /** Converte string do JSON para enum (case-insensitive). Ex: "mostruario" → MOSTRUARIO */
    @JsonCreator
    public static TipoFreezer fromString(String value) {
        if (value == null) return null;
        return switch (value.toLowerCase()) {
            case "mostruario" -> MOSTRUARIO;
            case "armazenamento" -> ARMAZENAMENTO;
            default -> throw new IllegalArgumentException("Tipo de Freezer inválido: " + value);
        };
    }

    /** Converte enum para string do JSON. Ex: MOSTRUARIO → "Mostruario" */
    @JsonValue
    public String toJson() {
        return switch (this) {
            case MOSTRUARIO -> "mostruario";
            case ARMAZENAMENTO -> "armazenamento";
        };
    }
}
