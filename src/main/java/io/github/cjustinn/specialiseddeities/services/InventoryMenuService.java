package io.github.cjustinn.specialiseddeities.services;

import io.github.cjustinn.specialiseddeities.enums.InventoryMenuType;
import io.github.cjustinn.specialiseddeities.models.custominventory.InventoryMenuHolder;
import io.github.cjustinn.specialiseddeities.models.custominventory.InventoryMenuLog;
import io.github.cjustinn.specialiseddeities.models.custominventory.ItemStackConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryMenuService {
    public static Map<String, InventoryMenuLog> activeInventoryMenus = new HashMap<>();

    public static <T> Inventory createInventory(final String uuid, final InventoryMenuType type, final @Nullable String subtitle, List<T> data, ItemStackConverter<T> converter) {
        final boolean multiPage = data.size() > 45;

        InventoryMenuLog<T> menuLog = new InventoryMenuLog(multiPage ? (int) Math.ceil(data.size() / 45) : 1, data, converter);
        activeInventoryMenus.put(uuid, menuLog);

        final int inventorySize = multiPage ? (9 * 6) : Math.max(9, ((int) Math.ceil(data.size() / 9) * 9));
        Inventory inventory = new InventoryMenuHolder(inventorySize, type, subtitle).getInventory();

        if (multiPage) {
            inventory.setItem(54, getNextButton(1, (int) Math.ceil(data.size() / 45)));
        }

        List<ItemStack> pageItems = menuLog.getCurrentItems();
        for (int i = 0; i < pageItems.size(); i++) {
            inventory.setItem(i, pageItems.get(i));
        }

        return inventory;
    }

    public static ItemStack getPrevButton(final int page, final int maxPages) {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
                Component.text(
                        String.format("Previous Page", NamedTextColor.RED)
                )
        );

        meta.lore(new ArrayList<TextComponent>() {{
            add(
                    Component.text(
                            String.format("%d / %d", page, maxPages),
                            NamedTextColor.GRAY
                    )
            );
        }});

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getNextButton(final int page, final int maxPages) {
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
                Component.text(
                        String.format("Next Page", NamedTextColor.GREEN)
                )
        );

        meta.lore(new ArrayList<TextComponent>() {{
            add(
                    Component.text(
                            String.format("%d / %d", page, maxPages),
                            NamedTextColor.GRAY
                    )
            );
        }});

        item.setItemMeta(meta);
        return item;
    }
}
