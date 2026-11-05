package fr.efrei.residence;

import java.util.ArrayList;
import java.util.List;

public class SmsService implements CanalNotification {

    static final int LONGUEUR_MIN_NUMERO = 10;
    static final int LONGUEUR_MAX_SMS = 160;
    static final String SUITE = "...";

    public static int compteurSms = 0;
    public static List<String[]> journal = new ArrayList<String[]>();

    public void envoyerSms(String numero, String message) {
        if (numero == null || numero.length() < LONGUEUR_MIN_NUMERO) {
            System.out.println("[SMS] numéro invalide : " + numero);
            return;
        }
        if (message.length() > LONGUEUR_MAX_SMS) {
            message = message.substring(0, LONGUEUR_MAX_SMS - SUITE.length()) + SUITE;
        }
        compteurSms++;
        journal.add(new String[]{numero, message});
        System.out.println("[SMS] -> " + numero + " : " + message);
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
