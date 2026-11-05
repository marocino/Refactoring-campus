package fr.efrei.residence;

import java.util.Optional;

/** Tout ce qui varie selon le profil de l'étudiant (patron Strategy). */
public interface RegleProfil {

    int MULTIPLE_SOLVABILITE = 3;
    double FRAIS_DOSSIER = 100;
    int PREAVIS_JOURS = 30;
    int PREAVIS_JOURS_REDUIT = 15;
    double TAUX_PENALITE = 0.05;
    double PENALITE_MINIMUM = 10;

    /** @return le motif de refus si les données d'inscription ne conviennent pas à ce profil */
    default Optional<String> verifierInscription(int echelonBourse, String telephone) {
        return Optional.empty();
    }

    /** Loyer après remise du profil (avant arrondi). */
    double appliquerRemise(double loyer, int echelonBourse);

    boolean estSolvable(double ressourcesMensuelles, double loyerChargesComprises);

    /** Dépôt de garantie, avant arrondi. */
    double caution(double loyerHorsCharges);

    double fraisDossier();

    int joursPreavis();

    /** Pénalité pour un loyer payé après l'échéance, avant arrondi. */
    default double penaliteRetard(double loyerChargesComprises) {
        return Math.max(loyerChargesComprises * TAUX_PENALITE, PENALITE_MINIMUM);
    }

    /** L'administration envoie une copie en anglais par email quand le canal préféré n'est pas l'email. */
    default boolean recoitCopieEnAnglais() {
        return false;
    }
}
