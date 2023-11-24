package io.github.cjustinn.specialiseddeities.commands.core;

import io.github.cjustinn.specialiseddeities.services.DeityService;
import io.github.cjustinn.specialiseddeities.services.LoggingService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DeityCoreCommandTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.addAll(
                    Arrays.stream(new String[] {
                            "pledge",
                            "create",
                            "abandon",
                            "status",
                            "altar",
                            "admin"
                    }).collect(Collectors.toList()).stream().filter((cmd) -> cmd.startsWith(args[0])).collect(Collectors.toList())
            );
        } else if (args.length == 2 && args[0].toLowerCase().equals("pledge")) {
            options.addAll(
                    DeityService.deities.values().stream().map((deity) -> deity.name).filter((deityName) -> deityName.startsWith(args[1])).collect(Collectors.toList())
            );
        } else if (args.length >= 2 && args[0].toLowerCase().equals("admin")) {
            // Admin commands
            // Root command
            if (args.length == 2) {
                options.addAll(
                        Arrays.stream(new String[] {
                                "reload"
                        }).collect(Collectors.toList()).stream().filter((cmd) -> cmd.startsWith(args[1])).collect(Collectors.toList())
                );
            }
        }

        return options;
    }
}
