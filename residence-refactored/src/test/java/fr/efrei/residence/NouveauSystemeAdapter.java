package fr.efrei.residence;

import fr.efrei.residence.contrat.DemandeAttribution;
import fr.efrei.residence.contrat.DemandeDevis;
import fr.efrei.residence.contrat.DemandeInscription;
import fr.efrei.residence.contrat.DemandeLogement;
import fr.efrei.residence.contrat.EtatBail;
import fr.efrei.residence.contrat.EtatLogement;
import fr.efrei.residence.contrat.Notification;
import fr.efrei.residence.contrat.SystemeResidence;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

/**
 * Branche le nouveau code sur les tests de contrat. Cette classe ne doit contenir que de la traduction.
 * <p>
 * Les types du contrat ({@code fr.efrei.residence.contrat.*}) et ceux du domaine portent les mêmes noms :
 * les premiers sont toujours écrits avec leur nom complet.
 */
public class NouveauSystemeAdapter implements SystemeResidence {

    private GestionResidence gestion;

    @Override
    public void reinitialiser() {
        // état statique : disparaîtra au palier 5
        Database.getInstance();
        Database.etudiants.clear();
        Database.baux.clear();
        EmailService.journal.clear();
        SmsService.journal.clear();
        CourrierService.journal.clear();
        SmsService.compteurSms = 0;
        CourrierService.totalAffranchissement = 0;
        Etudiant.compteur = 0;
        GestionResidence.compteurBail = 0;
        Residence.initialiser();
        gestion = new GestionResidence();
    }

    @Override
    public Optional<String> inscrire(DemandeInscription d) {
        Etudiant e = gestion.inscrireEtudiant(d.nom(), d.prenom(), d.email(), d.telephone(), d.adresse(),
                profil(d.profil()), d.echelonBourse(), d.ressourcesMensuelles(), d.pmr(), canal(d.canalPrefere()));
        return Optional.ofNullable(e).map(Etudiant::getId);
    }

    @Override
    public boolean ajouterLogement(DemandeLogement d) {
        int param = d.type() == fr.efrei.residence.contrat.TypeLogement.COLOCATION ? d.places() : d.surfaceBalcon();
        return gestion.ajouterLogement(type(d.type()), d.numero(), d.surface(), d.etage(), d.meuble(), param) != null;
    }

    @Override
    public Optional<EtatLogement> logement(String numero) {
        return Optional.ofNullable(Residence.trouver(numero)).map(l -> new EtatLogement(l.getNumero(),
                fr.efrei.residence.contrat.TypeLogement.valueOf(l.getType().name()), l.getNombrePlaces(),
                l.getNombreOccupants(), l.estAccessiblePMR()));
    }

    @Override
    public OptionalDouble devisLoyer(DemandeDevis d) {
        Logement l = Residence.trouver(d.numeroLogement());
        if (l == null) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(new CalculateurLoyer().calculerLoyer(l, profil(d.profil()), d.echelonBourse(),
                options(d.options())));
    }

    @Override
    public Optional<String> attribuer(DemandeAttribution d) {
        Etudiant e = etudiant(d.refEtudiant());
        if (e == null) {
            return Optional.empty();
        }
        Bail b = gestion.attribuerLogement(e, type(d.type()), options(d.options()), d.dateSignature());
        return Optional.ofNullable(b).map(Bail::getId);
    }

    @Override
    public Optional<EtatBail> consulter(String idBail) {
        return Optional.ofNullable(Database.baux.get(idBail)).map(b -> new EtatBail(b.getId(),
                b.getLogement().getNumero(), fr.efrei.residence.contrat.StatutBail.valueOf(b.getStatut().name()),
                b.getLoyerHorsCharges(), b.getCharges(), b.getLoyerChargesComprises(), b.getCaution(),
                b.getFraisDossier(), b.getDateFin(), b.soldeDu, b.cautionRestituee));
    }

    @Override
    public boolean entrer(String idBail, LocalDate date) {
        return gestion.entrerDansLesLieux(idBail, date);
    }

    @Override
    public boolean payer(String idBail, YearMonth mois, LocalDate date, double montant) {
        return gestion.payerLoyer(idBail, mois.toString(), date, montant);
    }

    @Override
    public boolean donnerPreavis(String idBail, LocalDate date) {
        return gestion.donnerPreavis(idBail, date);
    }

    @Override
    public boolean sortir(String idBail, LocalDate date, double retenuesDegats) {
        return gestion.sortir(idBail, date, retenuesDegats);
    }

    @Override
    public boolean annuler(String idBail) {
        return gestion.annulerBail(idBail);
    }

    @Override
    public Optional<String> quittance(String idBail, YearMonth mois) {
        return Optional.ofNullable(Database.baux.get(idBail)).map(b -> b.genererQuittance(mois.toString()));
    }

    @Override
    public void relancerImpayes() {
        gestion.relancerImpayes();
    }

    @Override
    public String rapport() {
        return gestion.genererRapport();
    }

    @Override
    public List<Notification> notifications() {
        List<Notification> notifications = new ArrayList<>();
        for (String[] email : EmailService.journal) {
            notifications.add(new Notification(fr.efrei.residence.contrat.Canal.EMAIL, email[0], email[1]));
        }
        for (String[] sms : SmsService.journal) {
            notifications.add(new Notification(fr.efrei.residence.contrat.Canal.SMS, sms[0], sms[1]));
        }
        for (String[] courrier : CourrierService.journal) {
            notifications.add(new Notification(fr.efrei.residence.contrat.Canal.COURRIER, courrier[0], courrier[1]));
        }
        return notifications;
    }

    // ------------------------------------------------------------------ traduction contrat -> domaine

    private static Etudiant etudiant(String ref) {
        for (Etudiant e : Database.etudiants.values()) {
            if (e.getId().equals(ref)) return e;
        }
        return null;
    }

    private static Profil profil(fr.efrei.residence.contrat.Profil p) {
        return Profil.valueOf(p.name());
    }

    private static Canal canal(fr.efrei.residence.contrat.Canal c) {
        return Canal.valueOf(c.name());
    }

    private static TypeLogement type(fr.efrei.residence.contrat.TypeLogement t) {
        return TypeLogement.valueOf(t.name());
    }

    private static Set<Option> options(Set<fr.efrei.residence.contrat.Option> options) {
        Set<Option> resultat = EnumSet.noneOf(Option.class);
        for (fr.efrei.residence.contrat.Option o : options) {
            resultat.add(Option.valueOf(o.name()));
        }
        return resultat;
    }
}
