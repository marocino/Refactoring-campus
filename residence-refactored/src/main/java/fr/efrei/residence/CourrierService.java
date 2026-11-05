package fr.efrei.residence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourrierService implements CanalNotification, SuiviAffranchissement {

    public static final double PRIX_TIMBRE = 1.29;

    private double totalAffranchissement = 0;
    private final List<Envoi> journal = new ArrayList<>();

    public void imprimerEtPoster(String nom, String adresse, String objet, String texte) {
        totalAffranchissement = totalAffranchissement + PRIX_TIMBRE;
        journal.add(new Envoi(adresse, objet));
        System.out.println("[COURRIER] " + nom + ", " + adresse + " | Objet : " + objet);
        System.out.println("        " + texte);
    }

    public List<Envoi> envois() {
        return Collections.unmodifiableList(journal);
    }

    @Override
    public double totalAffranchissement() {
        return totalAffranchissement;
    }

    @Override
    public void notifier(Etudiant destinataire, String sujet, String message) {
        imprimerEtPoster(destinataire.getNomComplet(), destinataire.getAdresse(), sujet, message);
    }

    @Override
    public void relancerImpaye(Etudiant destinataire, String idBail, double resteDu) {
        imprimerEtPoster(destinataire.getNomComplet(), destinataire.getAdresse(), "Relance loyer impayé",
                "Il reste " + Utils.euros(resteDu) + " à régler sur le bail " + idBail + ".");
    }
}
