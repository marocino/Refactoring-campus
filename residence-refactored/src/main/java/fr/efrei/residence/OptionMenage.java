package fr.efrei.residence;

/** Pourcentage qui dépend du type de logement : à appliquer avant les options à prix fixe. */
public class OptionMenage extends DecorateurLoyer {

    private final RegleTypeLogement regle;

    public OptionMenage(Loyer loyer, RegleTypeLogement regle) {
        super(loyer);
        this.regle = regle;
    }

    @Override
    public double montant() {
        return loyer.montant() * regle.coefMenage();
    }
}
