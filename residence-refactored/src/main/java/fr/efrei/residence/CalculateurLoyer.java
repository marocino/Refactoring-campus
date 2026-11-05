package fr.efrei.residence;

public class CalculateurLoyer {

    // majorations et minorations
    static final double COEF_MEUBLE = 1.15;
    static final double COEF_REZ_DE_CHAUSSEE = 0.95;
    static final int ETAGE_REZ_DE_CHAUSSEE = 0;

    // options à prix fixe
    static final double PRIX_PARKING = 45;
    static final double PRIX_LAVERIE = 12;
    static final double PRIX_ASSURANCE = 9;

    private final FabriqueRegleTypeLogement reglesType = new FabriqueRegleTypeLogement();
    private final FabriqueRegleProfil reglesProfil = new FabriqueRegleProfil();

    /**
     * Loyer mensuel hors charges, options comprises, remise déduite.
     */
    public double calculerLoyer(Logement l, Profil profil, int echelon, boolean parking, boolean menage,
                                boolean laverie, boolean fibre, boolean assurance) {
        RegleTypeLogement type = reglesType.pour(l.getType());
        double loyer = type.loyerDeBase(l);

        if (l.isMeuble()) loyer = loyer * COEF_MEUBLE;
        if (l.getEtage() == ETAGE_REZ_DE_CHAUSSEE) loyer = loyer * COEF_REZ_DE_CHAUSSEE;

        // options
        if (menage) loyer = loyer * type.coefMenage();
        if (parking) loyer = loyer + PRIX_PARKING;
        if (laverie) loyer = loyer + PRIX_LAVERIE;
        if (fibre) loyer = loyer + type.prixFibre();
        if (assurance) loyer = loyer + PRIX_ASSURANCE;

        loyer = reglesProfil.pour(profil).appliquerRemise(loyer, echelon);
        return Utils.arrondir(loyer);
    }

    public double calculerCharges(Logement l) {
        return reglesType.pour(l.getType()).charges();
    }
}
