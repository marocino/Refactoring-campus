package fr.efrei.residence;

import java.util.stream.Collectors;

/** Met en forme la quittance texte d'un mois payé. */
public class EditeurQuittance {

    static final String LIGNE_DOUBLE = "==========================================";
    static final String LIGNE_SIMPLE = "------------------------------------------";

    /** @param mois format AAAA-MM */
    public String generer(Bail bail, String mois) {
        return bail.paiementDe(mois)
                .map(p -> mettreEnForme(bail, mois, p))
                .orElse("Aucun paiement pour " + mois);
    }

    private String mettreEnForme(Bail bail, String mois, Paiement p) {
        Etudiant etudiant = bail.getEtudiant();
        Logement logement = bail.getLogement();
        StringBuilder q = new StringBuilder();
        q.append(LIGNE_DOUBLE).append('\n');
        q.append("   RÉSIDENCE CAMPUS VERT - QUITTANCE\n");
        q.append(LIGNE_DOUBLE).append('\n');
        q.append("Bail      : ").append(bail.getId()).append('\n');
        q.append("Locataire : ").append(etudiant.getNomComplet()).append(" (").append(etudiant.getProfil()).append(")\n");
        q.append("Logement  : ").append(logement.getNumero()).append(" - ").append(logement.getType()).append(' ')
                .append(logement.getSurface()).append(" m²");
        if (logement.isMeuble()) q.append(" meublé");
        q.append('\n');
        q.append("Période   : ").append(mois).append('\n');
        q.append("Options   : ").append(listerOptions(bail)).append('\n');
        q.append(LIGNE_SIMPLE).append('\n');
        q.append("Loyer HC  : ").append(Utils.euros(bail.getLoyerHorsCharges())).append('\n');
        q.append("Charges   : ").append(Utils.euros(bail.getCharges())).append('\n');
        if (p.penalite() > 0) {
            q.append("Pénalité  : ").append(Utils.euros(p.penalite())).append('\n');
        }
        q.append("Total dû  : ").append(Utils.euros(p.du())).append('\n');
        q.append("Payé le   : ").append(Utils.date(p.date())).append(" : ").append(Utils.euros(p.montant())).append('\n');
        if (p.estIncomplet()) {
            q.append("RESTE DÛ  : ").append(Utils.euros(Utils.arrondir(p.du() - p.montant()))).append('\n');
        }
        q.append(LIGNE_DOUBLE);
        return q.toString();
    }

    private static String listerOptions(Bail bail) {
        if (bail.getOptions().isEmpty()) return "aucune";
        return bail.getOptions().stream().map(Option::getLibelle).collect(Collectors.joining(", "));
    }
}
