package io.github.cjustinn.specialiseddeities.commands.core;

import io.github.cjustinn.specialiseddeities.SpecialisedDeities;
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
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length >= 1 && !args[0].toLowerCase().equals("admin")) {
                final Map<String, String> corePermissionMap = new HashMap<String, String>() {{
                    put("pledge", "specialiseddeities.pledge");
                    put("abandon", "specialiseddeities.pledge");
                    put("status", "specialiseddeities.pledge");
                    put("create", "specialiseddeities.create");
                    put("altar", "specialiseddeities.create");
                }};

                // Core user commands
                if (!corePermissionMap.containsKey(args[0].toLowerCase())) {
                    player.sendMessage(Component.text(
                            "That command does not exist!",
                            NamedTextColor.RED
                    ));

                    return true;
                } else if (!player.hasPermission(corePermissionMap.get(args[0].toLowerCase()))) {
                    player.sendMessage(Component.text(
                            "You do not have the required permissions to use that command!",
                            NamedTextColor.RED
                    ));

                    return true;
                } else {
                    // Select command
                    if (args[0].toLowerCase().equals("pledge")) {
                        if (DeityService.users.containsKey(player.getUniqueId().toString())) {
                            player.sendMessage(
                                    Component.text(
                                            String.format("You already have a patron deity!"),
                                            NamedTextColor.RED
                                    )
                            );
                        } else if (args.length == 2) {
                            // The player has specified a deity name, verify it's valid and add them to it.
                            if (!DeityService.deityExistsByName(args[1])) {
                                player.sendMessage(
                                        Component.text(
                                                String.format("No deity with the name %s exists. You can create them now!", args[1]),
                                                NamedTextColor.RED
                                        )
                                );
                            } else {
                                // Add the player as a follower of the provided deity.
                                final Deity namedDeity = DeityService.getDeityByName(args[1]);
                                if (DeityService.createUser(player.getUniqueId().toString(), namedDeity.id)) {
                                    player.sendMessage(
                                            Component.text(
                                                    String.format("You are now pledged to %s, %s %s.", namedDeity.name, namedDeity.gender.title, namedDeity.suffixOverride != null ? namedDeity.suffixOverride : namedDeity.getDomain().suffix),
                                                    NamedTextColor.GREEN
                                            )
                                    );
                                } else {
                                    player.sendMessage(
                                            Component.text(
                                                    String.format("Unable to register your pledge to %s, please try again!", namedDeity.name),
                                                    NamedTextColor.RED
                                            )
                                    );
                                }
                            }
                        } else if (args.length == 1) {
                            // If the player does not specify a deity name as a second argument, open the pledge menu.
                            if (DeityService.deities.size() == 0) {
                                player.sendMessage(
                                        Component.text(
                                                String.format("The server currently has no deities!"),
                                                NamedTextColor.RED
                                        )
                                );
                            } else {
                                player.openInventory(InventoryMenuService.createInventory(
                                        player.getUniqueId().toString(),
                                        InventoryMenuType.SelectDeity,
                                        null,
                                        DeityService.deities.values().stream().collect(Collectors.toList()),
                                        (Deity deity) -> deity.getDeityIcon()
                                ));
                            }
                        } else {
                            // Too many or too few arguments were provided. Display an error.
                            player.sendMessage(
                                    Component.text(
                                            String.format("You provided %s arguments for that command, please try again.", args.length > 2 ? "too many" : "too few"),
                                            NamedTextColor.RED
                                    )
                            );
                        }
                    }
                    // Create command
                    else if (args[0].toLowerCase().equals("create")) {
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
                    else if (args[0].toLowerCase().equals("status")) {
                        if (!DeityService.users.containsKey(player.getUniqueId().toString())) {
                            player.sendMessage(
                                    Component.text(
                                            String.format("You are not currently pledged to a deity!"),
                                            NamedTextColor.RED
                                    )
                            );
                        } else if (args.length > 1) {
                            player.sendMessage(
                                    Component.text(
                                            String.format("You have provided too many arguments for that command!"),
                                            NamedTextColor.RED
                                    )
                            );
                        } else {
                            final DeityUser user = DeityService.users.get(player.getUniqueId().toString());
                            player.sendMessage(
                                    Component.text(
                                            String.format("You are currently pledged to ")
                                    ).append(
                                            Component.text(
                                                    String.format("%s, %s %s ", user.getDeity().name, user.getDeity().gender.title, user.getDeity().suffixOverride != null ? user.getDeity().suffixOverride : user.getDeity().getDomain().suffix),
                                                    NamedTextColor.GOLD
                                            )
                                    ).append(
                                            Component.text(
                                                    String.format("(")
                                            )
                                    ).append(
                                            Component.text(
                                                    String.format("%d", user.getDeity().faithPoints),
                                                    NamedTextColor.GOLD
                                            )
                                    ).append(
                                            Component.text(
                                                    String.format("/%d).", PluginSettingsRepository.maxCollectiveFaith)
                                            )
                                    )
                            );
                        }
                    }
                    // Abandon Command
                    if (args[0].toLowerCase().equals("abandon")) {
                        if (!DeityService.users.containsKey(player.getUniqueId().toString())) {
                            player.sendMessage(
                                    Component.text(
                                            "You are not pledged to a deity!",
                                            NamedTextColor.RED
                                    )
                            );
                        } else if (args.length > 1) {
                            player.sendMessage(
                                    Component.text(
                                            "You have provided too many arguments to this command!",
                                            NamedTextColor.RED
                                    )
                            );
                        } else {
                            // Abandon the player's deity.
                            final Deity userDeity = DeityService.users.get(player.getUniqueId().toString()).getDeity();
                            if (DeityService.users.get(player.getUniqueId().toString()).abandonPledge()) {
                                DeityService.users.remove(player.getUniqueId().toString());
                                player.sendMessage(
                                        Component.text(
                                                String.format("You are no longer pledged to %s.", userDeity.name),
                                                NamedTextColor.GREEN
                                        )
                                );
                            } else {
                                player.sendMessage(
                                        Component.text(
                                                "Something went wrong while abandoning your pledge, please try again!",
                                                NamedTextColor.RED
                                        )
                                );
                            }
                        }
                    }
                }
            } else if (args.length >= 1 && args[0].toLowerCase().equals("admin")) {
                // Admin commands
                if (args.length >= 2) {
                    final Map<String, String> adminCommandPermissions = new HashMap<String, String>() {{
                        put("reload", "specialiseddeities.admin.reload");
                    }};

                    if (!adminCommandPermissions.containsKey(args[1].toLowerCase())) {
                        player.sendMessage(
                                Component.text(
                                        "That command does not exist!",
                                        NamedTextColor.RED
                                )
                        );
                    } else if (!player.hasPermission(adminCommandPermissions.get(args[1].toLowerCase()))) {
                        player.sendMessage(
                                Component.text(
                                        "You do not have sufficient permissions to run that command!",
                                        NamedTextColor.RED
                                )
                        );
                    } else {
                        // Reload command
                        if (args[1].toLowerCase().equals("reload")) {
                            if (SpecialisedDeities.plugin.reloadConfiguration()) {
                                player.sendMessage(
                                        Component.text(
                                                "SpecialisedDeities has been reloaded.",
                                                NamedTextColor.GREEN
                                        )
                                );
                            } else {
                                player.sendMessage(
                                        Component.text(
                                                "Failed to reload SpecialisedDeities configuration and data!",
                                                NamedTextColor.RED
                                        )
                                );
                            }
                        }
                    }
                } else {
                    // Root '/deities admin' command.
                    player.sendMessage(
                            Component.text(
                                    "That command does not exist!",
                                    NamedTextColor.RED
                            )
                    );
                }
            } else {
                // Root '/deities' command.
            }
        } else {
            LoggingService.writeLog(Level.SEVERE, "SpecialisedDeities commands cannot be run in the console!");
        }

        return true;
    }
}
