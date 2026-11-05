package fr.efrei.residence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmsService implements CanalNotification, SuiviSms {

    static final int LONGUEUR_MIN_NUMERO = 10;
    static final int LONGUEUR_MAX_SMS = 160;
    static final String SUITE = "...";

    private final List<Envoi> journal = new ArrayList<>();

    public void envoyerSms(String numero, String message) {
        if (numero == null || numero.length() < LONGUEUR_MIN_NUMERO) {
            System.out.println("[SMS] numéro invalide : " + numero);
            return;
        }
        if (message.length() > LONGUEUR_MAX_SMS) {
            message = message.substring(0, LONGUEUR_MAX_SMS - SUITE.length()) + SUITE;
        }
        journal.add(new Envoi(numero, message));
        System.out.println("[SMS] -> " + numero + " : " + message);
    }

    public List<Envoi> envois() {
        return Collections.unmodifiableList(journal);
    }

    @Override
    public int nombreSmsEnvoyes() {
        return journal.size();
    }

    @Override
    public void notifier(Etudiant destinataire, String sujet, String message) {
        envoyerSms(destinataire.getTelephone(), sujet + " - " + message);
    }

    @Override
    public void relancerImpaye(Etudiant destinataire, String idBail, double resteDu) {
        envoyerSms(destinataire.getTelephone(), "Campus Vert : " + Utils.euros(resteDu) + " impayés sur " + idBail);
    }
}
