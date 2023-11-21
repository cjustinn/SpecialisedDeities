package io.github.cjustinn.specialiseddeities.models.custominventory;

import io.github.cjustinn.specialiseddeities.enums.InventoryMenuType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryMenuHolder implements InventoryHolder {
    private Inventory inventory;
    private InventoryMenuType type;
    private @Nullable String subtitle;

    public InventoryMenuHolder(final int inventorySize, final InventoryMenuType inventoryType, final @Nullable String subtitle) {
        this.inventory = Bukkit.createInventory(
                this,
                inventorySize,
                subtitle == null ? Component.text(inventoryType.title) : Component.text(String.format("%s - %s", inventoryType.title, subtitle))
        );

        this.type = inventoryType;
        this.subtitle = subtitle;
    }

    public InventoryMenuType getMenuType() {
        return this.type;
    }

    public @Nullable String getSubtitle() {
        return this.subtitle;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
