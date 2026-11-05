package fr.efrei.residence;

public class RegleStandard implements RegleProfil {

    @Override
    public double appliquerRemise(double loyer, int echelonBourse) {
        return loyer;
    }

    @Override
    public boolean estSolvable(double ressourcesMensuelles, double loyerChargesComprises) {
        return ressourcesMensuelles >= loyerChargesComprises * MULTIPLE_SOLVABILITE;
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
        return PREAVIS_JOURS;
    }
}
