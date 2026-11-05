package fr.efrei.residence;

/** Seul endroit qui choisit la stratégie d'un profil. */
public class FabriqueRegleProfil {

    public RegleProfil pour(Profil profil) {
        return switch (profil) {
            case STANDARD -> new RegleStandard();
            case BOURSIER -> new RegleBoursier();
            case ALTERNANT -> new RegleAlternant();
            case INTERNATIONAL -> new RegleInternational();
        };
    }
}
