package fr.efrei.residence;

import java.util.List;

/** Données fixes de la résidence : son nom et son parc initial. */
public final class Residence {

    public static final String NOM = "Résidence Campus Vert";

    /** TYPE;NUMERO;SURFACE;ETAGE;MEUBLE;PARAM (PARAM = nb de places en coloc, surface du balcon en T2). */
    public static final List<String> CATALOGUE = List.of(
            "STUDIO;A001;26;0;true;0",
            "STUDIO;A102;22;1;true;0",
            "STUDIO;B103;20;1;false;0",
            "T1;A104;28;1;true;0",
            "T1;B105;30;1;false;0",
            "T2;A201;45;2;false;6",
            "T2;B202;48;2;true;0",
            "COLOCATION;B301;80;3;true;4",
            "COLOCATION;C001;63;0;true;3"
    );

    private Residence() {
    }
}
