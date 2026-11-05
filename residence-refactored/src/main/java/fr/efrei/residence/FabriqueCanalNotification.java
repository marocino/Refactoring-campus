package fr.efrei.residence;

/** Seul endroit qui sait quel service correspond à quel canal. */
public class FabriqueCanalNotification {

    public CanalNotification pour(Canal canal) {
        return switch (canal) {
            case EMAIL -> new EmailService();
            case SMS -> new SmsService();
            case COURRIER -> new CourrierService();
        };
    }
}
