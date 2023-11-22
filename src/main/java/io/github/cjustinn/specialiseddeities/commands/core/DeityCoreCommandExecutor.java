package io.github.cjustinn.specialiseddeities.commands.core;

import io.github.cjustinn.specialiseddeities.enums.InventoryMenuType;
import io.github.cjustinn.specialiseddeities.models.Deity;
import io.github.cjustinn.specialiseddeities.models.DeityDomain;
import io.github.cjustinn.specialiseddeities.models.DeityUser;
import io.github.cjustinn.specialiseddeities.repositories.PluginSettingsRepository;
import io.github.cjustinn.specialiseddeities.services.DeityCreatorService;
import io.github.cjustinn.specialiseddeities.services.DeityService;
import io.github.cjustinn.specialiseddeities.services.InventoryMenuService;
import io.github.cjustinn.specialiseddeities.services.LoggingService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DeityCoreCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Map<String, String> corePermissionMap = new HashMap<String, String>() {{
            put("pledge", "specialiseddeities.pledge");
            put("abandon", "specialiseddeities.pledge");
            put("status", "specialiseddeities.pledge");
            put("create", "specialiseddeities.create");
            put("altar", "specialiseddeities.create");
        }};

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length >= 1 && !corePermissionMap.containsKey(args[0].toLowerCase())) {
                player.sendMessage(Component.text(
                        "That command does not exist!",
                        NamedTextColor.RED
                ));

                return true;
            } else if (args.length >= 1 && !player.hasPermission(corePermissionMap.get(args[0].toLowerCase()))) {
                player.sendMessage(Component.text(
                        "You do not have the required permissions to use that command!",
                        NamedTextColor.RED
                ));

                return true;
            } else {
                // Select command
                if ((args.length == 1 && args[0].equals("pledge"))) {
                    if (DeityService.deities.size() > 0 && !DeityService.users.containsKey(player.getUniqueId().toString())) {
                        player.openInventory(InventoryMenuService.createInventory(
                                player.getUniqueId().toString(),
                                InventoryMenuType.SelectDeity,
                                null,
                                DeityService.deities.values().stream().collect(Collectors.toList()),
                                (Deity deity) -> deity.getDeityIcon()
                        ));
                    } else if (DeityService.deities.size() > 0 && DeityService.users.containsKey(player.getUniqueId().toString())) {
                        player.sendMessage(
                                Component.text(
                                        String.format("You already have a patron deity!"),
                                        NamedTextColor.RED
                                )
                        );
                    } else {
                        player.sendMessage(
                                Component.text(
                                        String.format("The server currently has no deities!"),
                                        NamedTextColor.RED
                                )
                        );
                    }
                }
                // Create command
                else if ((args.length >= 1 && args[0].equals("create"))) {
                    if (DeityService.users.containsKey(player.getUniqueId().toString())) {
                        player.sendMessage(
                                Component.text(
                                        String.format("You cannot create a new deity while already pledged to another!"),
                                        NamedTextColor.RED
                                )
                        );
                    } else if (args.length < 2) {
                        player.sendMessage(
                                Component.text(
                                        String.format("You must specify the deity name in order to create a new deity!"),
                                        NamedTextColor.RED
                                )
                        );
                    } else if (DeityService.deities.size() >= PluginSettingsRepository.maxGlobalDeities && PluginSettingsRepository.maxGlobalDeities != -1) {
                        player.sendMessage(
                                Component.text(
                                        String.format("The server has met it's configured maximum deity amount (%d)!", PluginSettingsRepository.maxGlobalDeities),
                                        NamedTextColor.RED
                                )
                        );
                    } else {
                        final String deityName = args[1];
                        final @Nullable String deityTitleOverride = args.length >= 3 ? String.join(" ", Arrays.stream(args).collect(Collectors.toList()).subList(2, args.length)) : null;

                        DeityCreatorService.beginCreation(player.getUniqueId().toString(), deityName, deityTitleOverride);
                        Inventory createInventory = InventoryMenuService.createInventory(player.getUniqueId().toString(), InventoryMenuType.CreateDeity, "Domain", DeityService.domains.stream().filter(
                                (domain) -> domain.maxDeities == -1 || (domain.maxDeities != -1 && DeityService.getDeityCountByDomain(domain.id) < domain.maxDeities)
                        ).collect(Collectors.toList()), (DeityDomain domain) -> domain.getIcon());
                        player.openInventory(createInventory);
                    }
                }
                // Status command
                else if (args.length == 1 && args[0].equals("status")) {
                    if (DeityService.users.containsKey(player.getUniqueId().toString())) {
                        final DeityUser user = DeityService.users.get(player.getUniqueId().toString());
                        player.sendMessage(
                                Component.text(
                                        String.format("You are currently pledged to ")
                                ).append(
                                        Component.text(
                                                String.format("%s, %s %s ", DeityService.deities.get(user.patronId).name, DeityService.deities.get(user.patronId).gender.title, DeityService.deities.get(user.patronId).suffixOverride != null ? DeityService.deities.get(user.patronId).suffixOverride : DeityService.deities.get(user.patronId).getDomain().suffix),
                                                NamedTextColor.GOLD
                                        )
                                ).append(
                                        Component.text(
                                                String.format("(")
                                        )
                                ).append(
                                        Component.text(
                                                String.format("%d", DeityService.deities.get(user.patronId).faithPoints),
                                                NamedTextColor.GOLD
                                        )
                                ).append(
                                        Component.text(
                                                String.format("/%d).", PluginSettingsRepository.maxCollectiveFaith)
                                        )
                                )
                        );
                    } else {
                        player.sendMessage(
                                Component.text(
                                        String.format("You are not currently pledged to a deity!"),
                                        NamedTextColor.RED
                                )
                        );
                    }
                }
            }
        } else {
            LoggingService.writeLog(Level.SEVERE, "Core SpecialisedDeities commands cannot be run in the console!");
        }

        return true;
    }
}
