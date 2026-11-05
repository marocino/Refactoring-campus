package fr.efrei.residence;

import java.util.EnumMap;
import java.util.Map;

/** Retrouve le service de notification configuré pour un canal. */
public class RepertoireCanaux {

    private final Map<Canal, CanalNotification> canaux;

    public RepertoireCanaux(Map<Canal, CanalNotification> canaux) {
        this.canaux = new EnumMap<>(canaux);
    }

    public CanalNotification pour(Canal canal) {
        CanalNotification service = canaux.get(canal);
        if (service == null) {
            throw new IllegalStateException("Aucun service configuré pour le canal " + canal);
        }
        return service;
    }
}
