package fr.efrei.residence;

/** Seul endroit qui sait construire chaque type de logement. */
public class FabriqueLogement {

    static final int PLACES_MIN_COLOCATION = 2;

    /**
     * @param param nombre de places d'une colocation, surface du balcon d'un T2, ignoré sinon
     */
    public Logement creer(TypeLogement type, String numero, double surface, int etage, boolean meuble, int param) {
        return switch (type) {
            case STUDIO -> new Studio(numero, surface, etage, meuble);
            case T1 -> new T1(numero, surface, etage, meuble);
            case T2 -> new T2(numero, surface, etage, meuble, param);
            case COLOCATION -> new Colocation(numero, surface, etage, meuble, Math.max(param, PLACES_MIN_COLOCATION));
        };
    }
}
