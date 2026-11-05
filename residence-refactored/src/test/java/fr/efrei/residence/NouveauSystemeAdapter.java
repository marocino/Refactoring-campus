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
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

/**
 * Branche le nouveau code sur les tests de contrat. Cette classe ne contient que de la traduction et
 * l'assemblage du graphe d'objets.
 * <p>
 * Les types du contrat ({@code fr.efrei.residence.contrat.*}) et ceux du domaine portent les mêmes noms :
 * les premiers sont toujours écrits avec leur nom complet.
 */
public class NouveauSystemeAdapter implements SystemeResidence {

    private EtudiantRepository etudiants;
    private BailRepository baux;
    private LogementRepository parc;
    private CalculateurLoyer calculateur;
    private EmailService email;
    private SmsService sms;
    private CourrierService courrier;
    private ServiceParc serviceParc;
    private ServiceInscription inscriptions;
    private ServiceAttribution attributions;
    private CycleDeVieBail cycle;
    private ServiceRelances relances;
    private GenerateurRapport rapport;
    private EditeurQuittance quittances;

    /** Repartir de zéro = construire un nouveau graphe d'objets. */
    @Override
    public void reinitialiser() {
        ConnexionBase connexion = new ConnexionBase();
        etudiants = new EtudiantRepositoryEnBase(connexion);
        baux = new BailRepositoryEnBase(connexion);
        parc = new LogementRepositoryEnMemoire();
        FabriqueLogement fabriqueLogement = new FabriqueLogement();
        new ChargeurCatalogue(fabriqueLogement).charger(Residence.CATALOGUE, parc);

        FabriqueRegleProfil reglesProfil = new FabriqueRegleProfil();
        FabriqueRegleTypeLogement reglesType = new FabriqueRegleTypeLogement();
        calculateur = new CalculateurLoyer(reglesType, reglesProfil);

        email = new EmailService();
        sms = new SmsService();
        courrier = new CourrierService();
        RepertoireCanaux canaux = new RepertoireCanaux(Map.of(
                Canal.EMAIL, email, Canal.SMS, sms, Canal.COURRIER, courrier));
        Notificateur notificateur = new Notificateur(canaux, reglesProfil);

        serviceParc = new ServiceParc(parc, fabriqueLogement);
        inscriptions = new ServiceInscription(new ValidateurInscription(etudiants, reglesProfil), etudiants,
                new CompteurIdentifiants(ServiceInscription.FORMAT_ID_ETUDIANT));
        attributions = new ServiceAttribution(parc, baux, calculateur, reglesProfil,
                new CompteurIdentifiants(ServiceAttribution.FORMAT_ID_BAIL), notificateur);
        cycle = new CycleDeVieBail(baux, reglesProfil, notificateur);
        relances = new ServiceRelances(baux, canaux);
        rapport = new GenerateurRapport(parc, baux, reglesType, sms, courrier);
        quittances = new EditeurQuittance();
    }

    @Override
    public Optional<String> inscrire(DemandeInscription d) {
        Etudiant e = inscriptions.inscrireEtudiant(d.nom(), d.prenom(), d.email(), d.telephone(), d.adresse(),
                profil(d.profil()), d.echelonBourse(), d.ressourcesMensuelles(), d.pmr(), canal(d.canalPrefere()));
        return Optional.ofNullable(e).map(Etudiant::getId);
    }

    @Override
    public boolean ajouterLogement(DemandeLogement d) {
        int param = d.type() == fr.efrei.residence.contrat.TypeLogement.COLOCATION ? d.places() : d.surfaceBalcon();
        return serviceParc.ajouterLogement(type(d.type()), d.numero(), d.surface(), d.etage(), d.meuble(), param) != null;
    }

    @Override
    public Optional<EtatLogement> logement(String numero) {
        return parc.trouver(numero).map(l -> new EtatLogement(l.getNumero(),
                fr.efrei.residence.contrat.TypeLogement.valueOf(l.getType().name()), l.getNombrePlaces(),
                l.getNombreOccupants(), l.estAccessiblePMR()));
    }

    @Override
    public OptionalDouble devisLoyer(DemandeDevis d) {
        return parc.trouver(d.numeroLogement())
                .map(l -> OptionalDouble.of(calculateur.calculerLoyer(l, profil(d.profil()), d.echelonBourse(),
                        options(d.options()))))
                .orElse(OptionalDouble.empty());
    }

    @Override
    public Optional<String> attribuer(DemandeAttribution d) {
        return etudiants.trouverParId(d.refEtudiant())
                .map(e -> attributions.attribuerLogement(e, type(d.type()), options(d.options()), d.dateSignature()))
                .map(Bail::getId);
    }

    @Override
    public Optional<EtatBail> consulter(String idBail) {
        return baux.trouver(idBail).map(b -> new EtatBail(b.getId(),
                b.getLogement().getNumero(), fr.efrei.residence.contrat.StatutBail.valueOf(b.getStatut().name()),
                b.getLoyerHorsCharges(), b.getCharges(), b.getLoyerChargesComprises(), b.getCaution(),
                b.getFraisDossier(), b.getDateFin(), b.getSoldeDu(), b.getCautionRestituee()));
    }

    @Override
    public boolean entrer(String idBail, LocalDate date) {
        return cycle.entrerDansLesLieux(idBail, date);
    }

    @Override
    public boolean payer(String idBail, YearMonth mois, LocalDate date, double montant) {
        return cycle.payerLoyer(idBail, mois.toString(), date, montant);
    }

    @Override
    public boolean donnerPreavis(String idBail, LocalDate date) {
        return cycle.donnerPreavis(idBail, date);
    }

    @Override
    public boolean sortir(String idBail, LocalDate date, double retenuesDegats) {
        return cycle.sortir(idBail, date, retenuesDegats);
    }

    @Override
    public boolean annuler(String idBail) {
        return cycle.annulerBail(idBail);
    }

    @Override
    public Optional<String> quittance(String idBail, YearMonth mois) {
        return baux.trouver(idBail).map(b -> quittances.generer(b, mois.toString()));
    }

    @Override
    public void relancerImpayes() {
        relances.relancerImpayes();
    }

    @Override
    public String rapport() {
        return rapport.genererRapport();
    }

    @Override
    public List<Notification> notifications() {
        List<Notification> notifications = new ArrayList<>();
        ajouter(notifications, fr.efrei.residence.contrat.Canal.EMAIL, email.envois());
        ajouter(notifications, fr.efrei.residence.contrat.Canal.SMS, sms.envois());
        ajouter(notifications, fr.efrei.residence.contrat.Canal.COURRIER, courrier.envois());
        return notifications;
    }

    // ------------------------------------------------------------------ traduction domaine <-> contrat

    private static void ajouter(List<Notification> notifications, fr.efrei.residence.contrat.Canal canal,
                                List<Envoi> envois) {
        for (Envoi envoi : envois) {
            notifications.add(new Notification(canal, envoi.destinataire(), envoi.objet()));
        }
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
