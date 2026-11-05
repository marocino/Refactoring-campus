package fr.efrei.residence;

import java.util.List;

/** Remplit le parc à partir d'un catalogue texte ({@code TYPE;NUMERO;SURFACE;ETAGE;MEUBLE;PARAM}). */
public class ChargeurCatalogue {

    private final FabriqueLogement fabrique;

    public ChargeurCatalogue(FabriqueLogement fabrique) {
        this.fabrique = fabrique;
    }

    public void charger(List<String> catalogue, LogementRepository parc) {
        for (String ligne : catalogue) {
            String[] t = ligne.split(";");
            String code = t[0];
            TypeLogement type = TypeLogement.depuisCode(code).orElse(null);
            if (type == null) {
                System.out.println("Type inconnu ignoré : " + code);
                continue;
            }
            parc.ajouter(fabrique.creer(type, t[1], Double.parseDouble(t[2]), Integer.parseInt(t[3]),
                    Boolean.parseBoolean(t[4]), Integer.parseInt(t[5])));
        }
    }
}
