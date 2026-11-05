package fr.efrei.residence;

/** Tout ce qui varie selon le type de logement (patron Strategy). */
public interface RegleTypeLogement {

    double CHARGES = 35;
    double COEF_MENAGE = 1.08;
    double PRIX_FIBRE = 20;

    /** Loyer de base, avant majorations, options et remise. */
    double loyerDeBase(Logement logement);

    /** Charges forfaitaires mensuelles. */
    default double charges() {
        return CHARGES;
    }

    /** Majoration multiplicative de l'option ménage. */
    default double coefMenage() {
        return COEF_MENAGE;
    }

    /** Prix mensuel de l'option fibre. */
    default double prixFibre() {
        return PRIX_FIBRE;
    }

    /** Début de ligne du rapport d'occupation, ex. {@code "Studios      : "}. */
    String libelleRapport();

    /** Unité affichée après le taux d'occupation dans le rapport. */
    default String uniteRapport() {
        return "";
    }
}
