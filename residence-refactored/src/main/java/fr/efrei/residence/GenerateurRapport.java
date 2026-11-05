package fr.efrei.residence;

/** Produit le rapport d'activité de la résidence. */
public class GenerateurRapport {

    private final LogementRepository parc;
    private final BailRepository baux;
    private final FabriqueRegleTypeLogement reglesType;
    private final SuiviSms suiviSms;
    private final SuiviAffranchissement suiviAffranchissement;

    public GenerateurRapport(LogementRepository parc, BailRepository baux, FabriqueRegleTypeLogement reglesType,
                             SuiviSms suiviSms, SuiviAffranchissement suiviAffranchissement) {
        this.parc = parc;
        this.baux = baux;
        this.reglesType = reglesType;
        this.suiviSms = suiviSms;
        this.suiviAffranchissement = suiviAffranchissement;
    }

    public String genererRapport() {
        StringBuilder r = new StringBuilder();
        r.append("=============== RAPPORT ").append(Residence.NOM).append(" ===============\n");
        int total = 0;
        int occupes = 0;
        for (TypeLogement type : TypeLogement.values()) {
            int places = 0;
            int occupants = 0;
            for (Logement l : parc.tous()) {
                if (l.getType() != type) continue;
                places += l.getNombrePlaces();
                occupants += l.getNombreOccupants();
            }
            RegleTypeLogement regle = reglesType.pour(type);
            r.append(regle.libelleRapport()).append(occupants).append('/').append(places).append(regle.uniteRapport())
                    .append('\n');
            total += places;
            occupes += occupants;
        }
        double surfaceBalcons = 0;
        for (Logement l : parc.tous()) {
            try {
                surfaceBalcons += l.getSurfaceBalcon();
            } catch (UnsupportedOperationException ex) {
            }
        }
        r.append("Occupation   : ").append(Math.round(occupes * 100.0 / total)).append(" %\n");
        r.append("Balcons      : ").append(surfaceBalcons).append(" m²\n");

        double loyersMensuels = 0;
        double impayes = 0;
        double cautions = 0;
        for (Bail b : baux.tous()) {
            if (b.getStatut().estEnCours()) {
                loyersMensuels += b.getLoyerChargesComprises();
                cautions += b.getCaution();
            }
            if (b.getSoldeDu() > 0) impayes += b.getSoldeDu();
        }
        r.append("Loyers CC    : ").append(Utils.euros(Utils.arrondir(loyersMensuels))).append(" / mois\n");
        r.append("Cautions     : ").append(Utils.euros(Utils.arrondir(cautions))).append('\n');
        r.append("Impayés      : ").append(Utils.euros(Utils.arrondir(impayes))).append('\n');
        r.append("SMS envoyés  : ").append(suiviSms.nombreSmsEnvoyes()).append('\n');
        r.append("Timbres      : ").append(Utils.euros(Utils.arrondir(suiviAffranchissement.totalAffranchissement())))
                .append('\n');
        r.append("============================================================");
        return r.toString();
    }
}
