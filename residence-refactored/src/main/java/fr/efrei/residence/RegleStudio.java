package fr.efrei.residence;

public class RegleStudio implements RegleTypeLogement {

    static final double PRIX_M2 = 14;
    static final double LOYER_MINIMUM = 350;

    @Override
    public double loyerDeBase(Logement logement) {
        return Math.max(logement.getSurface() * PRIX_M2, LOYER_MINIMUM);
    }

    @Override
    public String libelleRapport() {
        return "Studios      : ";
    }
}
