package fr.efrei.residence;

import java.util.Optional;

public class RegleInternational implements RegleProfil {

    static final String PREFIXE_TELEPHONE = "+";
    static final int MOIS_CAUTION = 2;
    static final double FRAIS_DOSSIER_INTERNATIONAL = 150;

    @Override
    public Optional<String> verifierInscription(int echelonBourse, String telephone) {
        if (telephone == null || !telephone.startsWith(PREFIXE_TELEPHONE)) {
            return Optional.of("numéro international (+XX) obligatoire");
        }
        return Optional.empty();
    }

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
        return loyerHorsCharges * MOIS_CAUTION;
    }

    @Override
    public double fraisDossier() {
        return FRAIS_DOSSIER_INTERNATIONAL;
    }

    @Override
    public int joursPreavis() {
        return PREAVIS_JOURS;
    }

    @Override
    public boolean recoitCopieEnAnglais() {
        return true;
    }
}
