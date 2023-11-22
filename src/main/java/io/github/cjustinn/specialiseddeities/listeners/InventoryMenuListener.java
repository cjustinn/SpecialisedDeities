package io.github.cjustinn.specialiseddeities.listeners;

import io.github.cjustinn.specialiseddeities.SpecialisedDeities;
import io.github.cjustinn.specialiseddeities.enums.DeityGender;
import io.github.cjustinn.specialiseddeities.enums.InventoryMenuType;
import io.github.cjustinn.specialiseddeities.models.DeityCreation;
import io.github.cjustinn.specialiseddeities.models.DeityDomain;
import io.github.cjustinn.specialiseddeities.models.custominventory.InventoryMenuHolder;
import io.github.cjustinn.specialiseddeities.models.custominventory.InventoryMenuLog;
import io.github.cjustinn.specialiseddeities.repositories.PluginSettingsRepository;
import io.github.cjustinn.specialiseddeities.services.DeityCreatorService;
import io.github.cjustinn.specialiseddeities.services.DeityService;
import io.github.cjustinn.specialiseddeities.services.InventoryMenuService;
import io.github.cjustinn.specialiseddeities.services.LoggingService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class InventoryMenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final String userId = ((Player) event.getWhoClicked()).getUniqueId().toString();
        InventoryView inventoryView = event.getView();
        InventoryHolder inventoryHolder = inventoryView.getTopInventory().getHolder();

        if (inventoryHolder instanceof InventoryMenuHolder) {
            // Cancel the event, since it is an inventory menu.
            event.setCancelled(true);

            // Get the clicked item.
            final ItemStack itemClicked = event.getCurrentItem();

            // If the inventory menu is of create type, handle it appropriately based on the sub-page open.
            if (((InventoryMenuHolder) inventoryHolder).getMenuType() == InventoryMenuType.CreateDeity && itemClicked != null) {
                // Get the inventory menu log for the current inventory menu.
                InventoryMenuLog log = InventoryMenuService.activeInventoryMenus.get(userId);
                if (log != null) {
                    // If the button pressed is a next or prev page button, handle their actions.
                    if (itemClicked == InventoryMenuService.getPrevButton(log.getCurrentPage(), log.getPages())) {
                        log.decrementPage();
                        List<ItemStack> newPageItems = log.getCurrentItems();

                        final int endSlotIndex = Math.min(46, inventoryView.getTopInventory().getSize());
                        for (int i = 0; i < endSlotIndex; i++) {
                            if (i < newPageItems.size()) {
                                inventoryView.getTopInventory().setItem(i, newPageItems.get(i));
                            } else {
                                inventoryView.getTopInventory().setItem(i, null);
                            }
                        }
                    } else if (itemClicked == InventoryMenuService.getNextButton(log.getCurrentPage(), log.getPages())) {
                        log.incrementPage();
                        List<ItemStack> newPageItems = log.getCurrentItems();

                        final int endSlotIndex = Math.min(46, inventoryView.getTopInventory().getSize());
                        for (int i = 0; i < endSlotIndex; i++) {
                            if (i < newPageItems.size()) {
                                inventoryView.getTopInventory().setItem(i, newPageItems.get(i));
                            } else {
                                inventoryView.getTopInventory().setItem(i, null);
                            }
                        }
                    }
                    // If the item clicked was a create option, handle it appropriately.
                    else if (((InventoryMenuHolder) inventoryHolder).getSubtitle().equals("Domain")) {
                        if (itemClicked.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(SpecialisedDeities.plugin, "DomainID"))) {
                            final String selectedDomain = itemClicked.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(SpecialisedDeities.plugin, "DomainID"), PersistentDataType.STRING);

                            DeityDomain domainObj = DeityService.getDomainById(selectedDomain);
                            if (domainObj != null) {
                                if (domainObj.maxDeities != -1 && DeityService.getDeityCountByDomain(domainObj.id) >= domainObj.maxDeities) {
                                    event.getWhoClicked().sendMessage(
                                            Component.text(
                                                    String.format("No more deities belonging to the chosen domain (%s) can be created!", String.format("Domain %s", domainObj.suffix)),
                                                    NamedTextColor.RED
                                            )
                                    );

                                    inventoryView.getTopInventory().close();
                                } else {
                                    List<DeityGender> genderOptions = new ArrayList<DeityGender>() {{
                                        add(DeityGender.Male);
                                        add(DeityGender.Female);

                                        if (PluginSettingsRepository.allowGenderlessDeities) {
                                            add(DeityGender.Genderless);
                                        }
                                    }};

                                    DeityCreatorService.setCreationValue(userId, "domain", selectedDomain);

                                    Inventory genderInventory = InventoryMenuService.createInventory(userId, InventoryMenuType.CreateDeity, "Gender", genderOptions, (DeityGender gender) -> gender.getIcon());
                                    event.getWhoClicked().openInventory(genderInventory);
                                }
                            }
                        }
                    } else if (((InventoryMenuHolder) inventoryHolder).getSubtitle().equals("Gender")) {
                        if (itemClicked.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(SpecialisedDeities.plugin, "GenderID"))) {
                            final int selectedGender = itemClicked.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(SpecialisedDeities.plugin, "GenderID"), PersistentDataType.INTEGER);
                            DeityCreatorService.setCreationValue(userId, "gender", DeityGender.getGenderById(selectedGender));

                            DeityCreation creationProcess = DeityCreatorService.activeCreations.get(userId);
                            List<PotionEffectType> availableEffects = DeityService.getDomainById(creationProcess.domain).allowedStatusEffects;

                            Inventory effectInventory = InventoryMenuService.createInventory(
                                    userId,
                                    InventoryMenuType.CreateDeity,
                                    "Status Effect",
                                    availableEffects,
                                    (PotionEffectType effectType) -> {
                                        DeityDomain targetDomain = DeityService.getDomainById(creationProcess.domain);
                                        if (targetDomain != null) {
                                            ItemStack icon = new ItemStack(targetDomain.iconData.type, 1);
                                            ItemMeta meta = icon.getItemMeta();
                                            meta.displayName(
                                                    Component.text(
                                                            String.format(effectType.getName()),
                                                            NamedTextColor.GOLD
                                                    )
                                            );

                                            meta.getPersistentDataContainer().set(
                                                    new NamespacedKey(SpecialisedDeities.plugin, "EffectID"),
                                                    PersistentDataType.STRING,
                                                    effectType.getName()
                                            );

                                            icon.setItemMeta(meta);
                                            return icon;
                                        } else {
                                            ItemStack defaultItem = new ItemStack(Material.PLAYER_HEAD, 1);
                                            ItemMeta meta = defaultItem.getItemMeta();
                                            meta.displayName(
                                                    Component.text(
                                                            String.format(effectType.getName()),
                                                            NamedTextColor.GOLD
                                                    )
                                            );

                                            meta.getPersistentDataContainer().set(
                                                    new NamespacedKey(SpecialisedDeities.plugin, "EffectID"),
                                                    PersistentDataType.STRING,
                                                    effectType.getName()
                                            );

                                            defaultItem.setItemMeta(meta);
                                            return defaultItem;
                                        }
                                    }
                            );

                            event.getWhoClicked().openInventory(effectInventory);
                        }
                    } else if (((InventoryMenuHolder) inventoryHolder).getSubtitle().equals("Status Effect")) {
                        if (itemClicked.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(SpecialisedDeities.plugin, "EffectID"))) {
                            final String selectedEffect = itemClicked.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(SpecialisedDeities.plugin, "EffectID"), PersistentDataType.STRING);
                            DeityCreatorService.setCreationValue(userId, "statusEffect", selectedEffect);

                            DeityCreation creationProcess = DeityCreatorService.activeCreations.get(userId);
                            List<Material> availableItems = DeityService.getDomainById(creationProcess.domain).allowedSacrificeItems;

                            Inventory itemInventory = InventoryMenuService.createInventory(
                                    userId,
                                    InventoryMenuType.CreateDeity,
                                    "Sacrifice Item",
                                    availableItems,
                                    (Material itemType) -> {
                                        ItemStack icon = new ItemStack(itemType, 1);
                                        ItemMeta meta = icon.getItemMeta();
                                        meta.getPersistentDataContainer().set(
                                                new NamespacedKey(SpecialisedDeities.plugin, "ItemID"),
                                                PersistentDataType.STRING,
                                                itemType.name()
                                        );

                                        icon.setItemMeta(meta);
                                        return icon;
                                    }
                            );

                            event.getWhoClicked().openInventory(itemInventory);
                        }
                    } else if (((InventoryMenuHolder) inventoryHolder).getSubtitle().equals("Sacrifice Item")) {
                        if (itemClicked.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(SpecialisedDeities.plugin, "ItemID"))) {
                            final String selectedItem = itemClicked.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(SpecialisedDeities.plugin, "ItemID"), PersistentDataType.STRING);
                            DeityCreatorService.setCreationValue(userId, "sacrificeItem", selectedItem);

                            DeityCreation creationProcess = DeityCreatorService.activeCreations.get(userId);
                            List<EntityType> availableMobs = DeityService.getDomainById(creationProcess.domain).allowedSacrificeEntityTypes;

                            Inventory mobInventory = InventoryMenuService.createInventory(
                                    userId,
                                    InventoryMenuType.CreateDeity,
                                    "Sacrifice Mob",
                                    availableMobs,
                                    (EntityType entityType) -> {
                                        ItemStack icon = new ItemStack(
                                                DeityCreation.entityItemMap.containsKey(entityType.name()) ? DeityCreation.entityItemMap.get(entityType.name()) : Material.PLAYER_HEAD,
                                                1
                                        );
                                        ItemMeta meta = icon.getItemMeta();
                                        meta.getPersistentDataContainer().set(
                                                new NamespacedKey(SpecialisedDeities.plugin, "EntityID"),
                                                PersistentDataType.STRING,
                                                entityType.name()
                                        );

                                        meta.displayName(
                                                Component.text(
                                                        DeityCreation.entityNameMap.getOrDefault(
                                                            entityType.name(),
                                                                StringUtils.join(Arrays.stream(entityType.name().split("_")).map((part) -> {
                                                                    return String.format("%s%s", part.substring(0, 1).toUpperCase(), part.substring(1).toLowerCase());
                                                                }).collect(Collectors.toList()), ' ')
                                                        ),
                                                        NamedTextColor.GOLD
                                                )
                                        );

                                        icon.setItemMeta(meta);

                                        return icon;
                                    }
                            );

                            event.getWhoClicked().openInventory(mobInventory);
                        }
                    } else if (((InventoryMenuHolder) inventoryHolder).getSubtitle().equals("Sacrifice Mob")) {
                        if (itemClicked.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(SpecialisedDeities.plugin, "EntityID"))) {
                            final String selectedMob = itemClicked.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(SpecialisedDeities.plugin, "EntityID"), PersistentDataType.STRING);
                            DeityCreatorService.setCreationValue(userId, "sacrificeMob", selectedMob);

                            DeityCreation creationProcess = DeityCreatorService.activeCreations.get(userId);
                            final boolean deityCreated = creationProcess.createDeity(inventoryView.getTopInventory());
                            if (deityCreated) {
                                inventoryView.getTopInventory().close();
                            } else {
                                event.getWhoClicked().sendMessage(
                                        Component.text(
                                                String.format("An error occurred while creating your deity. Please contact a server administrator for additional details and try again."),
                                                NamedTextColor.RED
                                        )
                                );
                            }
                        }
                    }
                }
            }
            // If the inventory menu is of select type, handle it appropriately.
            else if (((InventoryMenuHolder) inventoryHolder).getMenuType() == InventoryMenuType.SelectDeity && itemClicked != null) {

            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        final String userId = ((Player) event.getPlayer()).getUniqueId().toString();
        InventoryView view = event.getView();
        InventoryHolder holder = view.getTopInventory().getHolder();

        if (holder instanceof InventoryMenuHolder) {
            if (((InventoryMenuHolder) holder).getMenuType() == InventoryMenuType.CreateDeity && event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW) {
                DeityCreatorService.endCreation(userId);
            }
        }
    }

    private boolean titleContainsContent(InventoryView view, String content) {
        return ((TextComponent) view.title()).content().contains(content);
    }
}
