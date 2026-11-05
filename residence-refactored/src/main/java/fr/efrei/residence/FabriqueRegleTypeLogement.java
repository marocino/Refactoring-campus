package fr.efrei.residence;

/** Seul endroit qui choisit la stratégie d'un type de logement. */
public class FabriqueRegleTypeLogement {

    public RegleTypeLogement pour(TypeLogement type) {
        return switch (type) {
            case STUDIO -> new RegleStudio();
            case T1 -> new RegleT1();
            case T2 -> new RegleT2();
            case COLOCATION -> new RegleColocation();
        };
    }
}
