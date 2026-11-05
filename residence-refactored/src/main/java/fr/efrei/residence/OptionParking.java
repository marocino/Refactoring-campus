package fr.efrei.residence;

public class OptionParking extends DecorateurLoyer {

    static final double PRIX = 45;

    public OptionParking(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() + PRIX;
    }
}
