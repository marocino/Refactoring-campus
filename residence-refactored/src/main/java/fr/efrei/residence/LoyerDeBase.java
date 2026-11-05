package fr.efrei.residence;

/** Couche initiale : loyer de base selon le type de logement. */
public class LoyerDeBase implements Loyer {

    private final Logement logement;
    private final RegleTypeLogement regle;

    public LoyerDeBase(Logement logement, RegleTypeLogement regle) {
        this.logement = logement;
        this.regle = regle;
    }

    @Override
    public double montant() {
        return regle.loyerDeBase(logement);
    }
}
