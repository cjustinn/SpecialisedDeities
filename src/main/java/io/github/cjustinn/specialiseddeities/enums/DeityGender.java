package io.github.cjustinn.specialiseddeities.enums;

import io.github.cjustinn.specialiseddeities.SpecialisedDeities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public enum DeityGender {
    Male(0, "he", "God"),
    Female(1, "she", "Goddess"),
    Genderless(2, "they", "Deity");

    public final int genderId;
    public final String pronoun;
    public final String title;

    DeityGender(final int id, final String pronoun, final String title) {
        this.genderId = id;
        this.pronoun = pronoun;
        this.title = title;
    }

    public static DeityGender getGenderById(int id) {
        for (DeityGender gender : DeityGender.values()) {
            if (gender.genderId == id) {
                return gender;
            }
        }

        return null;
    }

    public ItemStack getIcon() {
        if (this.genderId == 0) {
            ItemStack icon = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1);
            ItemMeta meta = icon.getItemMeta();
            meta.displayName(
                    Component.text("Male", NamedTextColor.GOLD)
            );

            meta.getPersistentDataContainer().set(
                    new NamespacedKey(SpecialisedDeities.plugin, "GenderID"),
                    PersistentDataType.INTEGER,
                    this.genderId
            );

            icon.setItemMeta(meta);
            return icon;
        } else if (this.genderId == 1) {
            ItemStack icon = new ItemStack(Material.PINK_STAINED_GLASS_PANE, 1);
            ItemMeta meta = icon.getItemMeta();
            meta.displayName(
                    Component.text("Female", NamedTextColor.GOLD)
            );

            meta.getPersistentDataContainer().set(
                    new NamespacedKey(SpecialisedDeities.plugin, "GenderID"),
                    PersistentDataType.INTEGER,
                    this.genderId
            );

            icon.setItemMeta(meta);
            return icon;
        } else {

            ItemStack icon = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1);
            ItemMeta meta = icon.getItemMeta();
            meta.displayName(
                    Component.text("Genderless", NamedTextColor.GOLD)
            );

            meta.getPersistentDataContainer().set(
                    new NamespacedKey(SpecialisedDeities.plugin, "GenderID"),
                    PersistentDataType.INTEGER,
                    this.genderId
            );

            icon.setItemMeta(meta);
            return icon;
        }
    }
}
