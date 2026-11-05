package fr.efrei.residence;

import java.util.Arrays;
import java.util.Optional;

public enum TypeLogement {
    STUDIO, T1, T2, COLOCATION;

    /** Traduit un code texte (catalogue, saisie) ; vide si le type est inconnu. */
    public static Optional<TypeLogement> depuisCode(String code) {
        return Arrays.stream(values()).filter(t -> t.name().equals(code)).findFirst();
    }
}
