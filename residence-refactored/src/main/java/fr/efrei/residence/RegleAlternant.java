package fr.efrei.residence;

public class RegleAlternant implements RegleProfil {

    static final double COEF_REMISE = 0.90;
    static final int MULTIPLE_SOLVABILITE_ALTERNANT = 2;

    @Override
    public double appliquerRemise(double loyer, int echelonBourse) {
        return loyer * COEF_REMISE;
    }

    @Override
    public boolean estSolvable(double ressourcesMensuelles, double loyerChargesComprises) {
        return ressourcesMensuelles >= loyerChargesComprises * MULTIPLE_SOLVABILITE_ALTERNANT;
    }

    @Override
    public double caution(double loyerHorsCharges) {
        return loyerHorsCharges;
    }

    @Override
    public double fraisDossier() {
        return FRAIS_DOSSIER;
    }

    @Override
    public int joursPreavis() {
        return PREAVIS_JOURS_REDUIT;
    }
}
