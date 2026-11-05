package fr.efrei.residence;

/** Un moyen de joindre un étudiant. Chaque canal met le message dans son propre format. */
public interface CanalNotification {

    void notifier(Etudiant destinataire, String sujet, String message);

    /** Relance d'impayé, avec un texte propre au canal. */
    void relancerImpaye(Etudiant destinataire, String idBail, double resteDu);
}
