package fr.efrei.residence;

public enum StatutBail {
    SIGNE, ACTIF, PREAVIS, TERMINE, ANNULE;

    /** Le locataire occupe le logement et paie son loyer. */
    public boolean estEnCours() {
        return this == ACTIF || this == PREAVIS;
    }
}
