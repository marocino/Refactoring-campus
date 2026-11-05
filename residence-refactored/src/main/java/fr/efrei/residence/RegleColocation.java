package fr.efrei.residence;

public class RegleColocation implements RegleTypeLogement {

    static final double PRIX_M2 = 11;
    static final double CHARGES_COLOCATION = 25;
    static final double COEF_MENAGE_COLOCATION = 1.04; // parties communes déjà nettoyées
    static final double PRIX_FIBRE_COLOCATION = 0;     // fibre incluse

    /** Loyer par colocataire. */
    @Override
    public double loyerDeBase(Logement logement) {
        return logement.getSurface() * PRIX_M2 / logement.getNombrePlaces();
    }

    @Override
    public double charges() {
        return CHARGES_COLOCATION;
    }

    @Override
    public double coefMenage() {
        return COEF_MENAGE_COLOCATION;
    }

    @Override
    public double prixFibre() {
        return PRIX_FIBRE_COLOCATION;
    }

    @Override
    public String libelleRapport() {
        return "Colocation   : ";
    }

    @Override
    public String uniteRapport() {
        return " places";
    }
}
