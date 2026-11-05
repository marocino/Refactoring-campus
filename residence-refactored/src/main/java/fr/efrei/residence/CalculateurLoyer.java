package fr.efrei.residence;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

/** Assemble la chaîne de décorateurs du loyer : seul endroit qui connaît l'ordre des couches. */
public class CalculateurLoyer {

    static final int ETAGE_REZ_DE_CHAUSSEE = 0;

    private final FabriqueRegleTypeLogement reglesType = new FabriqueRegleTypeLogement();
    private final FabriqueRegleProfil reglesProfil = new FabriqueRegleProfil();

    /** Loyer mensuel hors charges, options comprises, remise déduite, arrondi au centime. */
    public double calculerLoyer(Logement l, Profil profil, int echelon, Set<Option> options) {
        return Utils.arrondir(assembler(l, profil, echelon, options).montant());
    }

    public double calculerCharges(Logement l) {
        return reglesType.pour(l.getType()).charges();
    }

    Loyer assembler(Logement l, Profil profil, int echelon, Set<Option> options) {
        RegleTypeLogement type = reglesType.pour(l.getType());
        Loyer loyer = new LoyerDeBase(l, type);
        if (l.isMeuble()) loyer = new MajorationMeuble(loyer);
        if (l.getEtage() == ETAGE_REZ_DE_CHAUSSEE) loyer = new MinorationRezDeChaussee(loyer);
        for (Map.Entry<Option, UnaryOperator<Loyer>> couche : couchesOptions(type).entrySet()) {
            if (options.contains(couche.getKey())) loyer = couche.getValue().apply(loyer);
        }
        return new RemiseProfil(loyer, reglesProfil.pour(profil), echelon);
    }

    /** Les options dans leur ordre d'application : le pourcentage (ménage) avant les prix fixes. */
    private static Map<Option, UnaryOperator<Loyer>> couchesOptions(RegleTypeLogement type) {
        Map<Option, UnaryOperator<Loyer>> couches = new LinkedHashMap<>();
        couches.put(Option.MENAGE, loyer -> new OptionMenage(loyer, type));
        couches.put(Option.PARKING, OptionParking::new);
        couches.put(Option.LAVERIE, OptionLaverie::new);
        couches.put(Option.FIBRE, loyer -> new OptionFibre(loyer, type));
        couches.put(Option.ASSURANCE, OptionAssurance::new);
        return couches;
    }
}
