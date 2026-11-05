package fr.efrei.residence;

import java.util.Optional;

/** Détermine si une demande d'inscription est recevable. */
public class ValidateurInscription {

    private final EtudiantRepository etudiants;
    private final FabriqueRegleProfil reglesProfil;

    public ValidateurInscription(EtudiantRepository etudiants, FabriqueRegleProfil reglesProfil) {
        this.etudiants = etudiants;
        this.reglesProfil = reglesProfil;
    }

    /** @return le motif du refus, vide si la demande est recevable */
    public Optional<String> motifDeRefus(String nom, String email, String telephone, String adresse, Profil profil,
                                         int echelonBourse, Canal canalPrefere) {
        if (nom == null || nom.trim().isEmpty()) {
            return Optional.of("nom obligatoire");
        }
        if (!Utils.emailValide(email)) {
            return Optional.of("email invalide (" + email + ")");
        }
        if (etudiants.trouverParEmail(email).isPresent()) {
            return Optional.of(email + " déjà inscrit");
        }
        if (profil == null) {
            return Optional.of("profil inconnu " + profil);
        }
        Optional<String> refusProfil = reglesProfil.pour(profil).verifierInscription(echelonBourse, telephone);
        if (refusProfil.isPresent()) {
            return refusProfil;
        }
        if (canalPrefere == Canal.SMS && telephone == null) {
            return Optional.of("téléphone obligatoire pour les SMS");
        }
        if (canalPrefere == Canal.COURRIER && (adresse == null || adresse.isEmpty())) {
            return Optional.of("adresse obligatoire pour le courrier");
        }
        return Optional.empty();
    }
}
