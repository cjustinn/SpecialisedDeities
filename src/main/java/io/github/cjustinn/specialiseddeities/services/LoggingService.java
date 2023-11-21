package io.github.cjustinn.specialiseddeities.services;

import io.github.cjustinn.specialiseddeities.SpecialisedDeities;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingService {
    private static final Logger logger = Logger.getLogger("Minecraft");

    public static void writeLog(Level level, String content) {
        final String name = SpecialisedDeities.plugin.getName();
        LoggingService.logger.log(
                level,
                String.format("[%s] %s", name, content)
        );
    }
}
