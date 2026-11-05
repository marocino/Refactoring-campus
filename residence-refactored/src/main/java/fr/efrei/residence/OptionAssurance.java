package fr.efrei.residence;

public class OptionAssurance extends DecorateurLoyer {

    static final double PRIX = 9;

    public OptionAssurance(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() + PRIX;
    }
}
