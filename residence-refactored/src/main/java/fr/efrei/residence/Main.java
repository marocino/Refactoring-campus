package fr.efrei.residence;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Map;

import static fr.efrei.residence.Option.ASSURANCE;
import static fr.efrei.residence.Option.FIBRE;
import static fr.efrei.residence.Option.LAVERIE;
import static fr.efrei.residence.Option.MENAGE;
import static fr.efrei.residence.Option.PARKING;

public class Main {

    public static void main(String[] args) {
        // ---------------------------------------------------------- assemblage du graphe d'objets
        ConnexionBase connexion = new ConnexionBase();
        EtudiantRepository etudiants = new EtudiantRepositoryEnBase(connexion);
        BailRepository baux = new BailRepositoryEnBase(connexion);
        LogementRepository parc = new LogementRepositoryEnMemoire();
        FabriqueLogement fabriqueLogement = new FabriqueLogement();
        new ChargeurCatalogue(fabriqueLogement).charger(Residence.CATALOGUE, parc);

        FabriqueRegleProfil reglesProfil = new FabriqueRegleProfil();
        FabriqueRegleTypeLogement reglesType = new FabriqueRegleTypeLogement();
        CalculateurLoyer calculateur = new CalculateurLoyer(reglesType, reglesProfil);

        SmsService sms = new SmsService();
        CourrierService courrier = new CourrierService();
        RepertoireCanaux canaux = new RepertoireCanaux(Map.of(
                Canal.EMAIL, new EmailService(), Canal.SMS, sms, Canal.COURRIER, courrier));
        Notificateur notificateur = new Notificateur(canaux, reglesProfil);

        ServiceParc serviceParc = new ServiceParc(parc, fabriqueLogement);
        ServiceInscription inscriptions = new ServiceInscription(new ValidateurInscription(etudiants, reglesProfil),
                etudiants, new CompteurIdentifiants(ServiceInscription.FORMAT_ID_ETUDIANT));
        ServiceAttribution attributions = new ServiceAttribution(parc, baux, calculateur, reglesProfil,
                new CompteurIdentifiants(ServiceAttribution.FORMAT_ID_BAIL), notificateur);
        CycleDeVieBail cycle = new CycleDeVieBail(baux, reglesProfil, notificateur);
        ServiceRelances relances = new ServiceRelances(baux, canaux);
        GenerateurRapport rapport = new GenerateurRapport(parc, baux, reglesType, sms, courrier);
        EditeurQuittance quittances = new EditeurQuittance();

        // ---------------------------------------------------------- scénario de démonstration
        serviceParc.ajouterLogement("STUDIO", "C002", 16, 0, false, 0);
        serviceParc.ajouterLogement("DUPLEX", "C003", 60, 1, true, 0);

        System.out.println();
        Etudiant alice = inscriptions.inscrireEtudiant("Martin", "Alice", "alice.martin@mail.fr", "0601020304", null,
                Profil.STANDARD, 0, 1500, false, Canal.EMAIL);
        Etudiant bilal = inscriptions.inscrireEtudiant("Haddad", "Bilal", "bilal.haddad@mail.fr", "0611223344", null,
                Profil.BOURSIER, 5, 450, false, Canal.SMS);
        Etudiant chloe = inscriptions.inscrireEtudiant("Durand", "Chloé", "chloe.durand@mail.fr", "0622334455", null,
                Profil.ALTERNANT, 0, 1100, true, Canal.EMAIL);
        Etudiant diego = inscriptions.inscrireEtudiant("Alvarez", "Diego", "diego.alvarez@mail.es", "+34612345678", null,
                Profil.INTERNATIONAL, 0, 3000, false, Canal.SMS);
        Etudiant emma = inscriptions.inscrireEtudiant("Petit", "Emma", "emma.petit@mail.fr", null,
                "12 rue des Lilas, 35000 Rennes", Profil.STANDARD, 0, 1300, false, Canal.COURRIER);
        Etudiant gaelle = inscriptions.inscrireEtudiant("Morel", "Gaëlle", "gaelle.morel@mail.fr", "0633445566", null,
                Profil.STANDARD, 0, 1000, false, Canal.EMAIL);
        Etudiant hugo = inscriptions.inscrireEtudiant("Leroy", "Hugo", "hugo.leroy@mail.fr", "0644556677", null,
                Profil.BOURSIER, 7, 300, true, Canal.EMAIL);
        inscriptions.inscrireEtudiant("Faure", "Farid", "farid.faure-at-mail.fr", "0655667788", null,
                Profil.STANDARD, 0, 1200, false, Canal.EMAIL);
        inscriptions.inscrireEtudiant("Wang", "Li", "li.wang@mail.cn", "0612121212", null,
                Profil.INTERNATIONAL, 0, 2000, false, Canal.EMAIL);
        inscriptions.inscrireEtudiant("Doublon", "Alice", "alice.martin@mail.fr", "0601020304", null,
                Profil.STANDARD, 0, 1500, false, Canal.EMAIL);

        System.out.println();
        LocalDate signature = LocalDate.of(2026, 8, 25);
        Bail bA = attributions.attribuerLogement(alice, TypeLogement.STUDIO, EnumSet.of(PARKING, ASSURANCE), signature);
        Bail bB = attributions.attribuerLogement(bilal, TypeLogement.COLOCATION, EnumSet.of(MENAGE, LAVERIE, FIBRE), signature);
        Bail bC = attributions.attribuerLogement(chloe, TypeLogement.T1, EnumSet.of(LAVERIE, FIBRE, ASSURANCE), signature);
        Bail bD = attributions.attribuerLogement(diego, TypeLogement.T2, EnumSet.of(PARKING, MENAGE, FIBRE, ASSURANCE), signature);
        Bail bE = attributions.attribuerLogement(emma, TypeLogement.COLOCATION, EnumSet.of(FIBRE), signature);
        Bail bG = attributions.attribuerLogement(gaelle, TypeLogement.STUDIO, EnumSet.of(MENAGE, FIBRE), signature);
        Bail bH = attributions.attribuerLogement(hugo, TypeLogement.COLOCATION, EnumSet.of(MENAGE, ASSURANCE), signature);

        System.out.println();
        LocalDate rentree = LocalDate.of(2026, 9, 1);
        cycle.entrerDansLesLieux(bA.getId(), rentree);
        cycle.entrerDansLesLieux(bB.getId(), rentree);
        cycle.entrerDansLesLieux(bC.getId(), rentree);
        cycle.entrerDansLesLieux(bD.getId(), rentree);
        cycle.entrerDansLesLieux(bE.getId(), LocalDate.of(2026, 8, 20));
        cycle.entrerDansLesLieux(bE.getId(), rentree);
        if (bH != null) cycle.annulerBail(bH.getId());
        if (bG != null) cycle.annulerBail(bG.getId());

        System.out.println();
        cycle.payerLoyer(bA.getId(), "2026-09", LocalDate.of(2026, 9, 3), bA.getLoyerChargesComprises());
        cycle.payerLoyer(bB.getId(), "2026-09", LocalDate.of(2026, 9, 12), bB.getLoyerChargesComprises());
        cycle.payerLoyer(bC.getId(), "2026-09", LocalDate.of(2026, 9, 5), bC.getLoyerChargesComprises());
        cycle.payerLoyer(bD.getId(), "2026-09", LocalDate.of(2026, 9, 9), bD.getLoyerChargesComprises());
        cycle.payerLoyer(bE.getId(), "2026-09", LocalDate.of(2026, 9, 2), 200);
        cycle.payerLoyer(bA.getId(), "2026-10", LocalDate.of(2026, 10, 15), 300);
        cycle.payerLoyer(bC.getId(), "2026-10", LocalDate.of(2026, 10, 1), 1000);

        System.out.println();
        System.out.println(quittances.generer(bD, "2026-09"));
        System.out.println(quittances.generer(bA, "2026-10"));

        System.out.println();
        relances.relancerImpayes();

        System.out.println();
        cycle.donnerPreavis(bB.getId(), LocalDate.of(2026, 10, 20));
        cycle.donnerPreavis(bD.getId(), LocalDate.of(2026, 10, 20));
        cycle.donnerPreavis(bE.getId(), LocalDate.of(2026, 10, 20));
        cycle.sortir(bD.getId(), LocalDate.of(2026, 11, 5), 0);
        cycle.sortir(bB.getId(), LocalDate.of(2026, 11, 5), 40);
        cycle.sortir(bD.getId(), LocalDate.of(2026, 11, 20), 120);
        cycle.sortir(bE.getId(), LocalDate.of(2026, 11, 20), 30);

        System.out.println();
        System.out.println(rapport.genererRapport());
    }
}
