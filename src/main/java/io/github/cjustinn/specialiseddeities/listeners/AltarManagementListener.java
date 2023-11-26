package io.github.cjustinn.specialiseddeities.listeners;

import io.github.cjustinn.specialiseddeities.enums.DeityAltarManagementType;
import io.github.cjustinn.specialiseddeities.models.DeityAltarManagementInteraction;
import io.github.cjustinn.specialiseddeities.services.DeityService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AltarManagementListener implements Listener {
    @EventHandler
    public void onBlockInteraction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!DeityService.altars.containsKey(event.getClickedBlock().getLocation())) {
            if (
                    DeityService.users.containsKey(player.getUniqueId().toString())
                            && DeityAltarManagementInteraction.activeInteractions.containsKey(event.getPlayer().getUniqueId().toString())
                            && DeityAltarManagementInteraction.activeInteractions.get(event.getPlayer().getUniqueId().toString()).altarLocation == null
                            && event.getAction() == Action.RIGHT_CLICK_BLOCK
            ) {
                event.setCancelled(true);

                DeityAltarManagementInteraction interactionObj = DeityAltarManagementInteraction.activeInteractions.get(event.getPlayer().getUniqueId().toString());
                interactionObj.altarLocation = event.getClickedBlock().getLocation();

                if (interactionObj.completeInteraction()) {
                    if (interactionObj.type == DeityAltarManagementType.Create) {
                        player.sendMessage(
                                Component.text(
                                        String.format(
                                                "You have made the block at (%d, %d, %d) an altar to %s!",
                                                event.getClickedBlock().getLocation().getBlockX(),
                                                event.getClickedBlock().getLocation().getBlockY(),
                                                event.getClickedBlock().getLocation().getBlockZ(),
                                                DeityService.users.get(player.getUniqueId().toString()).getDeity().name
                                        ),
                                        NamedTextColor.GREEN
                                )
                        );
                    } else {
                        player.sendMessage(
                                Component.text(
                                        String.format(
                                                "You have removed the altar to %s at (%d, %d, %d)!",
                                                DeityService.users.get(player.getUniqueId().toString()).getDeity().name,
                                                event.getClickedBlock().getLocation().getBlockX(),
                                                event.getClickedBlock().getLocation().getBlockY(),
                                                event.getClickedBlock().getLocation().getBlockZ()
                                        ),
                                        NamedTextColor.GREEN
                                )
                        );
                    }
                }
            }
        } else {
            player.sendMessage(
                    Component.text(
                            String.format(
                                    "An altar to %s, %s %s already exists at that location, please re-run the command and try again!",
                                    DeityService.altars.get(event.getClickedBlock().getLocation()).getDeity().name,
                                    DeityService.altars.get(event.getClickedBlock().getLocation()).getDeity().gender.title,
                                    DeityService.altars.get(event.getClickedBlock().getLocation()).getDeity().suffixOverride != null ? DeityService.altars.get(event.getClickedBlock().getLocation()).getDeity().suffixOverride : DeityService.altars.get(event.getClickedBlock().getLocation()).getDeity().getDomain().suffix
                            ),
                            NamedTextColor.RED
                    )
            );
        }
    }
}
