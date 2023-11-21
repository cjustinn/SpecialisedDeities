package io.github.cjustinn.specialiseddeities.services;

import io.github.cjustinn.specialiseddeities.models.DeityCreation;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DeityCreatorService {
    public static Map<String, DeityCreation> activeCreations = new HashMap<>();

    public static void beginCreation(final String uuid, final String name, final @Nullable String suffixOverride) {
        activeCreations.put(uuid, new DeityCreation(uuid, name, suffixOverride));
    }

    public static void endCreation(final String uuid) {
        activeCreations.remove(uuid);
    }

    public static <T> void setCreationValue(final String uuid, final String fieldName, final T value) {
        DeityCreation creation = activeCreations.get(uuid);

        try {
            Class<?> clazz = creation.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(creation, value);

            activeCreations.put(uuid, creation);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LoggingService.writeLog(Level.SEVERE, e.getMessage());
        }
    }
}
