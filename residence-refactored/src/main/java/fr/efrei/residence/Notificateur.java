package fr.efrei.residence;

/** Prévient un étudiant sur son canal préféré, avec la copie en anglais prévue pour son profil. */
public class Notificateur {

    static final String PREFIXE_EMAIL_ANGLAIS = "[EN] ";

    private final RepertoireCanaux canaux;
    private final FabriqueRegleProfil reglesProfil;

    public Notificateur(RepertoireCanaux canaux, FabriqueRegleProfil reglesProfil) {
        this.canaux = canaux;
        this.reglesProfil = reglesProfil;
    }

    public void notifier(Etudiant e, String sujet, String message) {
        Canal canal = e.getCanalPrefere();
        canaux.pour(canal).notifier(e, sujet, message);
        // les internationaux reçoivent toujours un email en plus (demande de l'administration)
        if (reglesProfil.pour(e.getProfil()).recoitCopieEnAnglais() && canal != Canal.EMAIL) {
            canaux.pour(Canal.EMAIL).notifier(e, PREFIXE_EMAIL_ANGLAIS + sujet, message);
        }
    }
}
