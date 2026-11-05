package fr.efrei.residence;

/** Un loyer mensuel hors charges, non arrondi, construit par couches successives (patron Decorator). */
@FunctionalInterface
public interface Loyer {

    double montant();
}
