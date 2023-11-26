package io.github.cjustinn.specialiseddeities.models;

import io.github.cjustinn.specialiseddeities.enums.DeityAltarManagementType;
import io.github.cjustinn.specialiseddeities.services.LoggingService;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DeityAltarManagementInteraction {
    public static Map<String, DeityAltarManagementInteraction> activeInteractions = new HashMap<String, DeityAltarManagementInteraction>();

    public final DeityAltarManagementType type;
    public final String playerUuid;
    public @Nullable Location altarLocation;

    public DeityAltarManagementInteraction(final DeityAltarManagementType managementType, final String player) {
        this.type = managementType;
        this.playerUuid = player;

        this.altarLocation = null;
    }

    public boolean completeInteraction() {
        if (this.altarLocation == null) {
            LoggingService.writeLog(Level.WARNING, String.format("Failed to complete %s altar interaction. Altar location not selected.", this.type == DeityAltarManagementType.Create ? "creation" : "removal"));
            return false;
        } else {
            if (this.type == DeityAltarManagementType.Create) {
                // Creation operation; add the altar to the database AND DeityService.
                // Temp logging statement.
                LoggingService.writeLog(Level.INFO, "Altar creation should now take place.");
            } else {
                // Removal operation; remove the altar from the database AND DeityService.
                // Temp logging statement.
                LoggingService.writeLog(Level.INFO, "Altar removal should now take place.");
            }

            return true;
        }
    }
}
