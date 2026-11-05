package fr.efrei.residence;

/** Options mensuelles choisies à la signature du bail, dans l'ordre où elles sont listées sur la quittance. */
public enum Option {
    MENAGE("ménage"), PARKING("parking"), LAVERIE("laverie"), FIBRE("fibre"), ASSURANCE("assurance");

    private final String libelle;

    Option(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
