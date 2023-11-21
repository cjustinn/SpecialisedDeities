package io.github.cjustinn.specialiseddeities.models;

import io.github.cjustinn.specialiseddeities.enums.DeityGender;
import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQuery;
import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQueryValueType;
import io.github.cjustinn.specialiseddeities.models.SQL.DatabaseQueryValue;
import io.github.cjustinn.specialiseddeities.services.DatabaseService;
import io.github.cjustinn.specialiseddeities.services.DeityService;
import io.github.cjustinn.specialiseddeities.services.LoggingService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DeityCreation {
    public static Map<String, String> entityNameMap = new HashMap<String, String>() {{
        put("MUSHROOM_COW", "Mooshroom");
    }};

    public static Map<String, Material> entityItemMap = new HashMap<String, Material>() {{
        put("AXOLOTL", Material.AXOLOTL_SPAWN_EGG);
        put("BAT", Material.BAT_SPAWN_EGG);
        put("BEE", Material.BEE_SPAWN_EGG);
        put("BLAZE", Material.BLAZE_SPAWN_EGG);
        put("CAMEL", Material.CAMEL_SPAWN_EGG);
        put("CAT", Material.CAT_SPAWN_EGG);
        put("CAVE_SPIDER", Material.CAVE_SPIDER_SPAWN_EGG);
        put("CHICKEN", Material.CHICKEN_SPAWN_EGG);
        put("COD", Material.COD_SPAWN_EGG);
        put("COW", Material.COW_SPAWN_EGG);
        put("CREEPER", Material.CREEPER_SPAWN_EGG);
        put("DOLPHIN", Material.DOLPHIN_SPAWN_EGG);
        put("DONKEY", Material.DONKEY_SPAWN_EGG);
        put("DROWNED", Material.DROWNED_SPAWN_EGG);
        put("ELDER_GUARDIAN", Material.ELDER_GUARDIAN_SPAWN_EGG);
        put("ENDERMAN", Material.ENDERMAN_SPAWN_EGG);
        put("ENDERMITE", Material.ENDERMITE_SPAWN_EGG);
        put("EVOKER", Material.EVOKER_SPAWN_EGG);
        put("FOX", Material.FOX_SPAWN_EGG);
        put("FROG", Material.FROG_SPAWN_EGG);
        put("GHAST", Material.GHAST_SPAWN_EGG);
        put("GLOW_SQUID", Material.GLOW_SQUID_SPAWN_EGG);
        put("GOAT", Material.GOAT_SPAWN_EGG);
        put("GUARDIAN", Material.GUARDIAN_SPAWN_EGG);
        put("HOGLIN", Material.HOGLIN_SPAWN_EGG);
        put("HORSE", Material.HORSE_SPAWN_EGG);
        put("HUSK", Material.HUSK_SPAWN_EGG);
        put("IRON_GOLEM", Material.IRON_GOLEM_SPAWN_EGG);
        put("LLAMA", Material.LLAMA_SPAWN_EGG);
        put("MAGMA_CUBE", Material.MAGMA_CUBE_SPAWN_EGG);
        put("MULE", Material.MULE_SPAWN_EGG);
        put("MUSHROOM_COW", Material.MOOSHROOM_SPAWN_EGG);
        put("OCELOT", Material.OCELOT_SPAWN_EGG);
        put("PANDA", Material.PANDA_SPAWN_EGG);
        put("PARROT", Material.PARROT_SPAWN_EGG);
        put("PHANTOM", Material.PHANTOM_SPAWN_EGG);
        put("PIG", Material.PIG_SPAWN_EGG);
        put("PIGLIN", Material.PIGLIN_SPAWN_EGG);
        put("PIGLIN_BRUTE", Material.PIGLIN_BRUTE_SPAWN_EGG);
        put("PILLAGER", Material.VINDICATOR_SPAWN_EGG);
        put("POLAR_BEAR", Material.POLAR_BEAR_SPAWN_EGG);
        put("PUFFERFISH", Material.PUFFERFISH_SPAWN_EGG);
        put("RABBIT", Material.RABBIT_SPAWN_EGG);
        put("RAVAGER", Material.RAVAGER_SPAWN_EGG);
        put("SALMON", Material.SALMON_SPAWN_EGG);
        put("SHEEP", Material.SHEEP_SPAWN_EGG);
        put("SHULKER", Material.SHULKER_SPAWN_EGG);
        put("SILVERFISH", Material.SILVERFISH_SPAWN_EGG);
        put("SKELETON", Material.SKELETON_SPAWN_EGG);
        put("SLIME", Material.SLIME_SPAWN_EGG);
        put("SNIFFER", Material.SNIFFER_EGG);
        put("SNOWMAN", Material.SNOWBALL);
        put("SPIDER", Material.SPIDER_SPAWN_EGG);
        put("SQUID", Material.SQUID_SPAWN_EGG);
        put("STRAY", Material.STRAY_SPAWN_EGG);
        put("STRIDER", Material.STRIDER_SPAWN_EGG);
        put("TADPOLE", Material.TADPOLE_SPAWN_EGG);
        put("TRADER_LLAMA", Material.TRADER_LLAMA_SPAWN_EGG);
        put("TROPICAL_FISH", Material.TROPICAL_FISH_SPAWN_EGG);
        put("TURTLE", Material.TURTLE_SPAWN_EGG);
        put("VILLAGER", Material.VILLAGER_SPAWN_EGG);
        put("VINDICATOR", Material.VINDICATOR_SPAWN_EGG);
        put("WANDERING_TRADER", Material.WANDERING_TRADER_SPAWN_EGG);
        put("WARDEN", Material.WARDEN_SPAWN_EGG);
        put("WITCH", Material.WITCH_SPAWN_EGG);
        put("WITHER", Material.WITHER_SPAWN_EGG);
        put("WITHER_SKELETON", Material.WITHER_SKELETON_SPAWN_EGG);
        put("WOLF", Material.WOLF_SPAWN_EGG);
        put("ZOGLIN", Material.ZOGLIN_SPAWN_EGG);
        put("ZOMBIE", Material.ZOMBIE_SPAWN_EGG);
        put("ZOMBIE_VILLAGER", Material.ZOMBIE_VILLAGER_SPAWN_EGG);
        put("ZOMBIFIED_PIGLIN", Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);
    }};

    public final String creatorId;
    public final String name;
    public final String suffixOverride;
    public @Nullable String domain;
    public @Nullable DeityGender gender;
    public @Nullable String statusEffect;
    public @Nullable String sacrificeItem;
    public @Nullable String sacrificeMob;

    public final boolean isProtected = false;
    public final boolean isGod = false;

    public DeityCreation(final String c, final String n, final @Nullable String sO) {
        this.creatorId = c;
        this.name = n;
        this.suffixOverride = sO;
    }

    public boolean createDeity(@Nullable Inventory menuInventory) {
        boolean success = false;

        @Nullable ResultSet createResult = DatabaseService.RunQuery(DatabaseQuery.CreateDeity, new DatabaseQueryValue[] {
                new DatabaseQueryValue(1, this.name, DatabaseQueryValueType.String),
                new DatabaseQueryValue(2, this.suffixOverride, DatabaseQueryValueType.String),
                new DatabaseQueryValue(3, this.domain, DatabaseQueryValueType.String),
                new DatabaseQueryValue(4, this.gender.genderId, DatabaseQueryValueType.Integer),
                new DatabaseQueryValue(5, this.sacrificeItem, DatabaseQueryValueType.String),
                new DatabaseQueryValue(6, this.sacrificeMob, DatabaseQueryValueType.String),
                new DatabaseQueryValue(7, this.statusEffect, DatabaseQueryValueType.String),
                new DatabaseQueryValue(8, this.isProtected, DatabaseQueryValueType.Boolean),
                new DatabaseQueryValue(9, this.creatorId, DatabaseQueryValueType.String),
                new DatabaseQueryValue(10, true, DatabaseQueryValueType.Boolean),
                new DatabaseQueryValue(11, this.isGod, DatabaseQueryValueType.Boolean)
        });

        if (createResult != null) {
            try {
                while (createResult.next()) {
                    Deity createdDeity = new Deity(
                            createResult.getInt(1),
                            createResult.getString(2),
                            createResult.getString(3),
                            createResult.getString(4),
                            DeityGender.getGenderById(createResult.getInt(5)),
                            Material.getMaterial(createResult.getString(6)),
                            EntityType.fromName(createResult.getString(7)),
                            PotionEffectType.getByName(createResult.getString(8)),
                            this.creatorId,
                            createResult.getBoolean(9),
                            createResult.getBoolean(10),
                            createResult.getDate(11),
                            createResult.getString(12),
                            createResult.getInt(13)
                    );

                    DeityService.deities.put(createdDeity.id, createdDeity);

                    LoggingService.writeLog(Level.INFO, String.format("Deity %s has been created.", createdDeity.name));

                    if (this.creatorId != "server") {
                        @Nullable ResultSet userResults = DatabaseService.RunQuery(DatabaseQuery.SelectUserById, new DatabaseQueryValue[] {
                                new DatabaseQueryValue(1, this.creatorId, DatabaseQueryValueType.String)
                        });

                        if (userResults != null) {
                            while (userResults.next()) {
                                DeityUser createdDeityUser = new DeityUser(
                                        userResults.getString(1),
                                        userResults.getInt(2),
                                        userResults.getBoolean(3),
                                        userResults.getBoolean(4),
                                        userResults.getBoolean(5),
                                        userResults.getDate(6)
                                );

                                DeityService.users.put(this.creatorId, createdDeityUser);

                                Bukkit.getPlayer(UUID.fromString(createdDeityUser.uuid)).sendMessage(
                                        Component.text(
                                                String.format("The new deity %s is now your patron deity!", createdDeity.name),
                                                NamedTextColor.GREEN
                                        )
                                );
                            }
                        }
                    }

                    if (menuInventory != null) {
                        menuInventory.close();
                    }

                    success = true;
                }
            } catch (SQLException e) {
                LoggingService.writeLog(Level.SEVERE, String.format("An error occurred while creating a new deity: %s", e.getMessage()));
            }
        }

        return success;
    }
}
