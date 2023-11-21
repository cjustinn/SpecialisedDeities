package io.github.cjustinn.specialiseddeities.models;

import io.github.cjustinn.specialiseddeities.SpecialisedDeities;
import io.github.cjustinn.specialiseddeities.enums.DeityDomainType;
import io.github.cjustinn.specialiseddeities.enums.DeityGender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.stream.Collectors;

public class DeityDomain {
    public final String id;
    public final String suffix;
    public final DeityIconData iconData;
    public final List<PotionEffectType> allowedStatusEffects;
    public final List<EntityType> allowedSacrificeEntityTypes;
    public final List<Material> allowedSacrificeItems;
    public final int maxDeities;

    public DeityDomain(ConfigurationSection config, final String key) {
        this.id = key;
        this.suffix = config.getString("suffix");
        this.iconData = new DeityIconData(
                config.getInt("icon.customModelData"),
                Material.getMaterial(config.getString("icon.type"))
        );

        this.allowedStatusEffects = config.getStringList("allowedStatusEffects").stream()
                .filter((effectName) -> PotionEffectType.getByName(effectName) != null)
                .map((effectName) -> PotionEffectType.getByName(effectName))
                .collect(Collectors.toList());
        this.allowedSacrificeItems = config.getStringList("allowedSacrificeItems").stream()
                .filter((itemName) -> Material.getMaterial(itemName) != null)
                .map((itemName) -> Material.getMaterial(itemName))
                .collect(Collectors.toList());
        this.allowedSacrificeEntityTypes = config.getStringList("allowedSacrificeMobs").stream()
                .filter((mobName) -> EntityType.valueOf(mobName) != null)
                .map((mobName) -> EntityType.valueOf(mobName))
                .collect(Collectors.toList());
        this.maxDeities = config.getInt("maxDeities");
    }

    public ItemStack getIcon() {
        ItemStack icon = this.iconData.getIcon();

        ItemMeta meta = icon.getItemMeta();
        meta.displayName(
                Component.text(
                        String.format("Domain %s", this.suffix),
                        NamedTextColor.GOLD
                )
        );

        meta.getPersistentDataContainer().set(
                new NamespacedKey(SpecialisedDeities.plugin, "DomainID"),
                PersistentDataType.STRING,
                this.id
        );

        icon.setItemMeta(meta);

        return icon;
    }
}
