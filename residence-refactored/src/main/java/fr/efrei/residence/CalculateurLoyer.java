package fr.efrei.residence;

public class CalculateurLoyer {

    // loyer de base
    static final double PRIX_M2_STUDIO = 14;
    static final double LOYER_MINIMUM_STUDIO = 350;
    static final double PRIX_M2_T1 = 13;
    static final double FORFAIT_T1 = 50;
    static final double PRIX_M2_T2 = 12;
    static final double FORFAIT_T2 = 100;
    static final double PRIX_M2_COLOCATION = 11;

    // majorations et minorations
    static final double COEF_MEUBLE = 1.15;
    static final double COEF_REZ_DE_CHAUSSEE = 0.95;
    static final int ETAGE_REZ_DE_CHAUSSEE = 0;

    // options
    static final double COEF_MENAGE = 1.08;
    static final double COEF_MENAGE_COLOCATION = 1.04;
    static final double PRIX_PARKING = 45;
    static final double PRIX_LAVERIE = 12;
    static final double PRIX_FIBRE = 20;
    static final double PRIX_ASSURANCE = 9;

    // remises
    static final double REMISE_PAR_ECHELON = 0.05;
    static final double REMISE_BOURSIER_MAX = 0.30;
    static final double REMISE_ALTERNANT = 0.10;

    // charges
    static final double CHARGES = 35;
    static final double CHARGES_COLOCATION = 25;

    /**
     * Loyer mensuel hors charges, options comprises, remise déduite.
     */
    public double calculerLoyer(Logement l, Profil profil, int echelon, boolean parking, boolean menage,
                                boolean laverie, boolean fibre, boolean assurance) {
        // loyer de base selon le type
        double loyer = switch (l.getType()) {
            case STUDIO -> Math.max(l.getSurface() * PRIX_M2_STUDIO, LOYER_MINIMUM_STUDIO);
            case T1 -> l.getSurface() * PRIX_M2_T1 + FORFAIT_T1;
            case T2 -> l.getSurface() * PRIX_M2_T2 + FORFAIT_T2;
            case COLOCATION -> l.getSurface() * PRIX_M2_COLOCATION / l.getNombrePlaces();
        };

        if (l.isMeuble()) loyer = loyer * COEF_MEUBLE;
        if (l.getEtage() == ETAGE_REZ_DE_CHAUSSEE) loyer = loyer * COEF_REZ_DE_CHAUSSEE;

        boolean colocation = l.getType() == TypeLogement.COLOCATION;
        // options
        if (menage) {
            loyer = loyer * (colocation ? COEF_MENAGE_COLOCATION : COEF_MENAGE); // parties communes déjà nettoyées
        }
        if (parking) loyer = loyer + PRIX_PARKING;
        if (laverie) loyer = loyer + PRIX_LAVERIE;
        if (fibre && !colocation) loyer = loyer + PRIX_FIBRE; // fibre incluse en coloc
        if (assurance) loyer = loyer + PRIX_ASSURANCE;

        // remise selon le profil
        if (profil == Profil.BOURSIER) {
            double taux = Math.min(echelon * REMISE_PAR_ECHELON, REMISE_BOURSIER_MAX);
            loyer = loyer * (1 - taux);
        } else if (profil == Profil.ALTERNANT) {
            loyer = loyer * (1 - REMISE_ALTERNANT);
        }

        return Utils.arrondir(loyer);
    }

    public double calculerCharges(Logement l) {
        return l.getType() == TypeLogement.COLOCATION ? CHARGES_COLOCATION : CHARGES;
    }
}
