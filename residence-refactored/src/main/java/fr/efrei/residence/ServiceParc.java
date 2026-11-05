package fr.efrei.residence;

/** Ajoute de nouveaux logements au parc. */
public class ServiceParc {

    private final LogementRepository parc;
    private final FabriqueLogement fabrique;

    public ServiceParc(LogementRepository parc, FabriqueLogement fabrique) {
        this.parc = parc;
        this.fabrique = fabrique;
    }

    /** Ajout à partir d'un code texte (saisie) : un type inconnu est refusé. */
    public Logement ajouterLogement(String codeType, String numero, double surface, int etage, boolean meuble,
                                    int param) {
        TypeLogement type = TypeLogement.depuisCode(codeType).orElse(null);
        if (type == null && parc.trouver(numero).isEmpty()) {
            System.out.println("Type de logement inconnu : " + codeType);
            return null;
        }
        return ajouterLogement(type, numero, surface, etage, meuble, param);
    }

    /** @return le logement ajouté, ou {@code null} si le numéro existe déjà */
    public Logement ajouterLogement(TypeLogement type, String numero, double surface, int etage, boolean meuble,
                                    int param) {
        if (parc.trouver(numero).isPresent()) {
            System.out.println("Logement " + numero + " existe déjà");
            return null;
        }
        Logement l = fabrique.creer(type, numero, surface, etage, meuble, param);
        parc.ajouter(l);
        System.out.println("Logement ajouté : " + numero + " (" + type + ")");
        return l;
    }
}
