package fr.efrei.residence;

import java.util.Optional;

/** Enregistre les nouveaux étudiants dont la demande est recevable. */
public class ServiceInscription {

    public static final String FORMAT_ID_ETUDIANT = "ETU-%d";

    private final ValidateurInscription validateur;
    private final EtudiantRepository etudiants;
    private final CompteurIdentifiants identifiants;

    public ServiceInscription(ValidateurInscription validateur, EtudiantRepository etudiants,
                              CompteurIdentifiants identifiants) {
        this.validateur = validateur;
        this.etudiants = etudiants;
        this.identifiants = identifiants;
    }

    /** @return l'étudiant inscrit, ou {@code null} si l'inscription est refusée (le motif est affiché) */
    public Etudiant inscrireEtudiant(String nom, String prenom, String email, String telephone, String adresse,
                                     Profil profil, int echelonBourse, double ressources, boolean pmr,
                                     Canal canalPrefere) {
        Optional<String> refus = validateur.motifDeRefus(nom, email, telephone, adresse, profil, echelonBourse,
                canalPrefere);
        if (refus.isPresent()) {
            System.out.println("Inscription refusée : " + refus.get());
            return null;
        }
        Etudiant e = new Etudiant(identifiants.suivant(), nom, prenom, email, telephone, adresse, profil,
                echelonBourse, ressources, pmr, canalPrefere);
        etudiants.sauvegarder(e);
        System.out.println("Étudiant inscrit : " + e.getId() + " " + e.getNomComplet() + " (" + profil + ")");
        return e;
    }
}
