package fr.efrei.residence;

import java.time.LocalDate;
import java.util.Set;

/** Signe un bail sur le premier logement du parc qui convient à l'étudiant. */
public class ServiceAttribution {

    public static final String FORMAT_ID_BAIL = "BAIL-%03d";

    private final LogementRepository parc;
    private final BailRepository baux;
    private final CalculateurLoyer calculateur;
    private final FabriqueRegleProfil reglesProfil;
    private final CompteurIdentifiants identifiants;
    private final Notificateur notificateur;

    public ServiceAttribution(LogementRepository parc, BailRepository baux, CalculateurLoyer calculateur,
                              FabriqueRegleProfil reglesProfil, CompteurIdentifiants identifiants,
                              Notificateur notificateur) {
        this.parc = parc;
        this.baux = baux;
        this.calculateur = calculateur;
        this.reglesProfil = reglesProfil;
        this.identifiants = identifiants;
        this.notificateur = notificateur;
    }

    /** @return le bail signé, ou {@code null} si aucun logement ne convient */
    public Bail attribuerLogement(Etudiant e, TypeLogement typeSouhaite, Set<Option> options,
                                  LocalDate dateSignature) {
        System.out.println("--- Attribution d'un " + typeSouhaite + " à " + e.getNomComplet());
        RegleProfil regle = reglesProfil.pour(e.getProfil());
        Logement choisi = null;
        double loyer = 0;
        double charges = 0;
        for (Logement l : parc.tous()) {
            if (l.getType() != typeSouhaite) continue;
            if (!l.estLibre()) continue;
            if (e.isPmr() && !l.estAccessiblePMR()) continue;

            double loyerCandidat = calculateur.calculerLoyer(l, e.getProfil(), e.getEchelonBourse(), options);
            double chargesCandidat = calculateur.calculerCharges(l);
            double total = loyerCandidat + chargesCandidat;

            if (!regle.estSolvable(e.getRessourcesMensuelles(), total)) {
                System.out.println("  " + l.getNumero() + " : ressources insuffisantes (" + Utils.euros(total) + "/mois)");
                continue;
            }
            choisi = l;
            loyer = loyerCandidat;
            charges = chargesCandidat;
            break;
        }
        if (choisi == null) {
            System.out.println("  Aucun logement disponible pour " + e.getNomComplet());
            return null;
        }

        if (choisi instanceof Colocation) {
            choisi.ajouterColocataire(e);
        } else {
            choisi.loger(e);
        }

        String id = identifiants.suivant();
        Bail b = new Bail(id, e, choisi, dateSignature, options);
        b.setLoyerHorsCharges(loyer);
        b.setCharges(charges);
        b.setCaution(Utils.arrondir(regle.caution(loyer)));
        b.setFraisDossier(regle.fraisDossier());

        baux.sauvegarder(b);
        System.out.println("  Bail " + id + " signé : " + choisi.getNumero() + ", " + Utils.euros(b.getLoyerChargesComprises())
                + " CC/mois, caution " + Utils.euros(b.getCaution()) + ", frais de dossier " + Utils.euros(b.getFraisDossier()));
        notificateur.notifier(e, "Votre bail " + id,
                "Bienvenue à la " + Residence.NOM + " ! Logement " + choisi.getNumero() + ", loyer "
                        + Utils.euros(b.getLoyerChargesComprises()) + " charges comprises.");
        return b;
    }
}
