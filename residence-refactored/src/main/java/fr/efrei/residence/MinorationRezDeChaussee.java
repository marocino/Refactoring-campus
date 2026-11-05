package fr.efrei.residence;

public class MinorationRezDeChaussee extends DecorateurLoyer {

    static final double COEF = 0.95;

    public MinorationRezDeChaussee(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() * COEF;
    }
}
