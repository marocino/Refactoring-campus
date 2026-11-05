package fr.efrei.residence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmailService implements CanalNotification {

    public static final String SMTP_HOST = "smtp.campusvert.fr";
    public static final String EXPEDITEUR = "gestion@campusvert.fr";

    // garde une trace des emails envoyés (demandé par l'administration)
    private final List<Envoi> journal = new ArrayList<>();

    /** Un email à une adresse invalide n'est pas envoyé. */
    public void envoyer(String destinataire, String sujet, String corps) {
        if (!Utils.emailValide(destinataire)) {
            return;
        }
        System.out.println("[EMAIL] " + EXPEDITEUR + " -> " + destinataire + " | " + sujet);
        System.out.println("        " + corps);
        journal.add(new Envoi(destinataire, sujet));
    }

    public List<Envoi> envois() {
        return Collections.unmodifiableList(journal);
    }

    @Override
    public void notifier(Etudiant destinataire, String sujet, String message) {
        envoyer(destinataire.getEmail(), sujet, message);
    }

    @Override
    public void relancerImpaye(Etudiant destinataire, String idBail, double resteDu) {
        envoyer(destinataire.getEmail(), "RELANCE - loyer impayé", "Bonjour " + destinataire.getPrenom()
                + ", il reste " + Utils.euros(resteDu) + " à régler sur le bail " + idBail + ".");
    }
}
