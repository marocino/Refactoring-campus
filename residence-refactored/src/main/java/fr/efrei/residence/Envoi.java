package fr.efrei.residence;

/**
 * Trace d'un message parti sur un canal.
 *
 * @param destinataire adresse email, numéro de téléphone ou adresse postale
 * @param objet        sujet de l'email ou du courrier, texte complet du SMS
 */
public record Envoi(String destinataire, String objet) {
}
