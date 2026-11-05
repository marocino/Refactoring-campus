package fr.efrei.residence;

/** Prix qui dépend du type de logement (incluse en colocation). */
public class OptionFibre extends DecorateurLoyer {

    private final RegleTypeLogement regle;

    public OptionFibre(Loyer loyer, RegleTypeLogement regle) {
        super(loyer);
        this.regle = regle;
    }

    @Override
    public double montant() {
        return loyer.montant() + regle.prixFibre();
    }
}
