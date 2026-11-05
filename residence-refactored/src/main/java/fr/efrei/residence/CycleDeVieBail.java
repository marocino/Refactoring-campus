package fr.efrei.residence;

import java.time.LocalDate;
import java.time.YearMonth;

/** Fait passer un bail signé d'un statut à l'autre (entrée, paiements, préavis, sortie, annulation). */
public class CycleDeVieBail {

    static final int JOUR_ECHEANCE = 5;

    private final BailRepository baux;
    private final FabriqueRegleProfil reglesProfil;
    private final Notificateur notificateur;

    public CycleDeVieBail(BailRepository baux, FabriqueRegleProfil reglesProfil, Notificateur notificateur) {
        this.baux = baux;
        this.reglesProfil = reglesProfil;
        this.notificateur = notificateur;
    }

    public boolean entrerDansLesLieux(String idBail, LocalDate date) {
        Bail b = baux.trouver(idBail).orElse(null);
        if (b == null || b.getStatut() != StatutBail.SIGNE) {
            System.out.println("Entrée impossible pour " + idBail);
            return false;
        }
        if (date.isBefore(b.getDateSignature())) {
            System.out.println("Entrée impossible avant la signature du bail");
            return false;
        }
        b.setDateEntree(date);
        b.setStatut(StatutBail.ACTIF);
        baux.mettreAJour(b);
        System.out.println("Entrée dans les lieux : " + idBail + " le " + Utils.date(date));
        notificateur.notifier(b.getEtudiant(), "État des lieux d'entrée",
                "Votre état des lieux d'entrée du logement " + b.getLogement().getNumero() + " est fait le "
                        + Utils.date(date) + ". Remise des clés effectuée. Pensez à souscrire une assurance habitation si ce n'est pas déjà fait.");
        return true;
    }

    /** @param mois format AAAA-MM */
    public boolean payerLoyer(String idBail, String mois, LocalDate datePaiement, double montant) {
        Bail b = baux.trouver(idBail).orElse(null);
        if (b == null || !b.getStatut().estEnCours()) {
            System.out.println("Paiement refusé pour " + idBail);
            return false;
        }
        LocalDate echeance = YearMonth.parse(mois).atDay(JOUR_ECHEANCE);
        double penalite = 0;
        if (datePaiement.isAfter(echeance)) {
            penalite = Utils.arrondir(reglesProfil.pour(b.getEtudiant().getProfil())
                    .penaliteRetard(b.getLoyerChargesComprises()));
        }
        double du = Utils.arrondir(b.getLoyerChargesComprises() + penalite);
        Paiement paiement = new Paiement(mois, datePaiement, montant, penalite, du);
        b.enregistrerPaiement(paiement);
        baux.mettreAJour(b);

        System.out.println("Paiement " + idBail + " " + mois + " : " + Utils.euros(montant) + " / " + Utils.euros(du)
                + (penalite > 0 ? " (dont pénalité " + Utils.euros(penalite) + ")" : ""));
        if (paiement.estIncomplet()) {
            notificateur.notifier(b.getEtudiant(), "Paiement incomplet " + mois,
                    "Nous avons reçu " + Utils.euros(montant) + " sur " + Utils.euros(du) + ". Reste dû : "
                            + Utils.euros(Utils.arrondir(du - montant)) + ".");
        } else {
            notificateur.notifier(b.getEtudiant(), "Quittance " + mois, "Merci, votre loyer de " + mois + " est réglé.");
        }
        return true;
    }

    public boolean donnerPreavis(String idBail, LocalDate date) {
        Bail b = baux.trouver(idBail).orElse(null);
        if (b == null || b.getStatut() != StatutBail.ACTIF) {
            System.out.println("Préavis impossible pour " + idBail);
            return false;
        }
        int jours = reglesProfil.pour(b.getEtudiant().getProfil()).joursPreavis();
        b.setDateFin(date.plusDays(jours));
        b.setStatut(StatutBail.PREAVIS);
        baux.mettreAJour(b);
        System.out.println("Préavis " + idBail + " : fin du bail le " + Utils.date(b.getDateFin()));
        notificateur.notifier(b.getEtudiant(), "Préavis de départ",
                "Votre préavis de " + jours + " jours est enregistré. Fin du bail le " + Utils.date(b.getDateFin()) + ".");
        return true;
    }

    public boolean sortir(String idBail, LocalDate date, double retenuesDegats) {
        Bail b = baux.trouver(idBail).orElse(null);
        if (b == null || b.getStatut() != StatutBail.PREAVIS) {
            System.out.println("Sortie impossible pour " + idBail);
            return false;
        }
        if (date.isBefore(b.getDateFin())) {
            System.out.println("Sortie impossible avant la fin du préavis (" + Utils.date(b.getDateFin()) + ")");
            return false;
        }
        Logement l = b.getLogement();
        if (l instanceof Colocation) {
            l.retirerColocataire(b.getEtudiant());
            l.nettoyerPartiesCommunes();
        } else {
            l.liberer(b.getEtudiant());
        }

        double impayes = b.getSoldeDu() > 0 ? b.getSoldeDu() : 0;
        double restitution = b.getCaution() - retenuesDegats - impayes;
        double resteAPayer = 0;
        if (restitution < 0) {
            resteAPayer = -restitution;
            restitution = 0;
        }
        b.solderAuDepart(retenuesDegats, restitution, resteAPayer);
        b.setStatut(StatutBail.TERMINE);
        baux.mettreAJour(b);

        System.out.println("Sortie " + idBail + " le " + Utils.date(date) + " : caution " + Utils.euros(b.getCaution())
                + ", retenues " + Utils.euros(b.getRetenues()) + ", impayés " + Utils.euros(impayes)
                + " -> restitué " + Utils.euros(b.getCautionRestituee()));
        String message = "Votre dépôt de garantie : " + Utils.euros(b.getCautionRestituee()) + " vous sera restitué sous 30 jours.";
        if (resteAPayer > 0) {
            message = message + " Il reste " + Utils.euros(b.getSoldeDu()) + " à régler.";
        }
        notificateur.notifier(b.getEtudiant(), "Sortie du logement " + l.getNumero(), message);
        return true;
    }

    public boolean annulerBail(String idBail) {
        Bail b = baux.trouver(idBail).orElse(null);
        if (b == null || b.getStatut() != StatutBail.SIGNE) {
            System.out.println("Annulation impossible pour " + idBail);
            return false;
        }
        Logement l = b.getLogement();
        if (l instanceof Colocation) {
            l.retirerColocataire(b.getEtudiant());
        } else {
            l.liberer(b.getEtudiant());
        }
        b.setStatut(StatutBail.ANNULE);
        baux.mettreAJour(b);
        System.out.println("Bail " + idBail + " annulé, frais de dossier conservés : " + Utils.euros(b.getFraisDossier()));
        notificateur.notifier(b.getEtudiant(), "Annulation du bail " + idBail,
                "Votre bail est annulé. Les frais de dossier (" + Utils.euros(b.getFraisDossier()) + ") restent acquis.");
        return true;
    }
}
