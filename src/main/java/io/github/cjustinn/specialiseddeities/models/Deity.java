package io.github.cjustinn.specialiseddeities.models;

import io.github.cjustinn.specialiseddeities.SpecialisedDeities;
import io.github.cjustinn.specialiseddeities.enums.DeityGender;
import io.github.cjustinn.specialiseddeities.services.DeityService;
import io.github.cjustinn.specialiseddeities.services.LoggingService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

public class Deity {
    public final int id;
    public final String domainId;
    public final String name;
    public final @Nullable String suffixOverride;
    public final DeityGender gender;
    public final PotionEffectType effect;
    public final EntityType sacrificeEntity;
    public final Material sacrificeItem;
    public final String leaderId;
    public final String creatorId;
    public final Date created;
    public final boolean isProtected;
    public boolean isActive;
    public int faithPoints;

    public Deity(
            int deityId,
            String deityName,
            @Nullable String deityTitleOverride,
            String deityDomain,
            @NotNull DeityGender deityGender,
            @Nullable Material deitySacrificeItem,
            @Nullable EntityType deitySacrificeEntity,
            @Nullable PotionEffectType deityStatusEffect,
            String deityCreator,
            boolean deityActive,
            boolean deityProtected,
            Date deityCreated,
            @Nullable String deityLeader,
            int deityFaithPoints
    ) {
        this.id = deityId;
        this.name = deityName;
        this.suffixOverride = deityTitleOverride;
        this.domainId = deityDomain;
        this.gender = deityGender;
        this.sacrificeItem = deitySacrificeItem;
        this.sacrificeEntity = deitySacrificeEntity;
        this.effect = deityStatusEffect;
        this.creatorId = deityCreator;
        this.isActive = deityActive;
        this.isProtected = deityProtected;
        this.created = deityCreated;
        this.leaderId = deityLeader;
        this.faithPoints = deityFaithPoints;

        if (deitySacrificeEntity == null) {
            LoggingService.writeLog(Level.WARNING, String.format(
                    "Deity %s has an invalid sacrifice entity. Entity sacrificing will be disabled for this deity until fixed.",
                    this.name
            ));
        }

        if (deitySacrificeItem == null) {
            LoggingService.writeLog(Level.WARNING, String.format(
                    "Deity %s has an invalid sacrifice item. Item sacrificing will be disabled for this deity until fixed.",
                    this.name
            ));
        }

        if (deityStatusEffect == null) {
            LoggingService.writeLog(Level.WARNING, String.format(
                    "Deity %s has an invalid associated status effect. Collective faith points for this deity will be ineligible for redemption until fixed.",
                    this.name
            ));
        }
    }

    public DeityDomain getDomain() {
        return DeityService.getDomainById(this.domainId);
    }

    public ItemStack getDeityIcon() {
        final String deityTitlePrefix = this.gender.title;
        final String deityTitle = this.suffixOverride != null ? this.suffixOverride : this.getDomain().suffix;

        ItemStack icon = this.getDomain().getIcon();
        ItemMeta meta = icon.getItemMeta();

        meta.displayName(
                Component.text(
                        String.format(this.name),
                        NamedTextColor.GOLD
                )
        );

        meta.lore(new ArrayList<Component>() {{
            add(Component.text(
                    String.format("%s %s", deityTitlePrefix, deityTitle),
                    NamedTextColor.GRAY
            ));
        }});

        meta.getPersistentDataContainer().set(
                new NamespacedKey(SpecialisedDeities.plugin, "DeityID"),
                PersistentDataType.INTEGER,
                this.id
        );

        icon.setItemMeta(meta);
        return icon;
    }
}
