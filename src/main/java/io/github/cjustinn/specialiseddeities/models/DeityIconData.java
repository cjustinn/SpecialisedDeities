package io.github.cjustinn.specialiseddeities.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DeityIconData {
    public final int customModelData;
    public final Material type;

    public DeityIconData(final int cmd, final Material item) {
        this.customModelData = cmd;
        this.type = item;
    }

    public ItemStack getIcon() {
        ItemStack iconItem = new ItemStack(this.type, 1);

        ItemMeta meta = iconItem.getItemMeta();
        meta.setCustomModelData(this.customModelData);

        iconItem.setItemMeta(meta);
        return iconItem;
    }
}
