package fr.efrei.residence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Bail {

    private final String id;
    private final Etudiant etudiant;
    private final Logement logement;
    private final LocalDate dateSignature;
    private LocalDate dateEntree;
    private LocalDate dateFin;
    private StatutBail statut;

    private double loyerHorsCharges;
    private double charges;
    private double caution;
    private double fraisDossier;

    private final Set<Option> options;

    private final List<Paiement> paiements = new ArrayList<>();
    private double soldeDu = 0;
    private double retenues = 0;
    private double cautionRestituee = 0;

    public Bail(String id, Etudiant etudiant, Logement logement, LocalDate dateSignature, Set<Option> options) {
        this.id = id;
        this.etudiant = etudiant;
        this.logement = logement;
        this.dateSignature = dateSignature;
        this.options = options.isEmpty() ? EnumSet.noneOf(Option.class) : EnumSet.copyOf(options);
        this.statut = StatutBail.SIGNE;
    }

    /** Options choisies, dans l'ordre de déclaration de {@link Option}. */
    public Set<Option> getOptions() { return Collections.unmodifiableSet(options); }

    public String getId() { return id; }
    public Etudiant getEtudiant() { return etudiant; }
    public Logement getLogement() { return logement; }
    public LocalDate getDateSignature() { return dateSignature; }
    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate d) { this.dateEntree = d; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate d) { this.dateFin = d; }
    public StatutBail getStatut() { return statut; }
    public void setStatut(StatutBail statut) { this.statut = statut; }
    public double getLoyerHorsCharges() { return loyerHorsCharges; }
    public void setLoyerHorsCharges(double l) { this.loyerHorsCharges = l; }
    public double getCharges() { return charges; }
    public void setCharges(double c) { this.charges = c; }
    public double getLoyerChargesComprises() { return Utils.arrondir(loyerHorsCharges + charges); }
    public double getCaution() { return caution; }
    public void setCaution(double c) { this.caution = c; }
    public double getFraisDossier() { return fraisDossier; }
    public void setFraisDossier(double f) { this.fraisDossier = f; }

    /** Solde restant dû ; négatif en cas d'avance. */
    public double getSoldeDu() { return soldeDu; }
    public double getRetenues() { return retenues; }
    public double getCautionRestituee() { return cautionRestituee; }

    public List<Paiement> getPaiements() { return Collections.unmodifiableList(paiements); }

    /** Ajoute le paiement ; l'écart entre le dû et le payé est reporté sur le solde. */
    public void enregistrerPaiement(Paiement p) {
        paiements.add(p);
        soldeDu = Utils.arrondir(soldeDu + p.du() - p.montant());
    }

    /** Dernier paiement enregistré pour ce mois (format AAAA-MM). */
    public Optional<Paiement> paiementDe(String mois) {
        Paiement trouve = null;
        for (Paiement p : paiements) {
            if (p.mois().equals(mois)) trouve = p;
        }
        return Optional.ofNullable(trouve);
    }

    /** Arrêté des comptes au départ : retenues appliquées, dépôt restitué, reste à payer. */
    public void solderAuDepart(double retenues, double cautionRestituee, double resteDu) {
        this.retenues = Utils.arrondir(retenues);
        this.cautionRestituee = Utils.arrondir(cautionRestituee);
        this.soldeDu = Utils.arrondir(resteDu);
    }
}
