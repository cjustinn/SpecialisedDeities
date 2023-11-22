package io.github.cjustinn.specialiseddeities;

import io.github.cjustinn.specialiseddeities.commands.core.DeityCoreCommandExecutor;
import io.github.cjustinn.specialiseddeities.commands.core.DeityCoreCommandTabCompleter;
import io.github.cjustinn.specialiseddeities.enums.DeityGender;
import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQuery;
import io.github.cjustinn.specialiseddeities.listeners.InventoryMenuListener;
import io.github.cjustinn.specialiseddeities.models.Deity;
import io.github.cjustinn.specialiseddeities.models.DeityDomain;
import io.github.cjustinn.specialiseddeities.models.DeityUser;
import io.github.cjustinn.specialiseddeities.models.SQL.MySQLCredentials;
import io.github.cjustinn.specialiseddeities.repositories.PluginSettingsRepository;
import io.github.cjustinn.specialiseddeities.services.DatabaseService;
import io.github.cjustinn.specialiseddeities.services.DeityService;
import io.github.cjustinn.specialiseddeities.services.LoggingService;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public final class SpecialisedDeities extends JavaPlugin {
    public static SpecialisedDeities plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        SpecialisedDeities.plugin = this;

        if (this.initialisePlugin()) {

        } else {
            this.getServer().getPluginManager().disablePlugin(this);
            LoggingService.writeLog(Level.SEVERE, "Failed to initialise SpecialisedDeities. Disabling plugin...");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean initialisePlugin() {
        List<Boolean> statuses = new ArrayList<>();

        statuses.add(this.initialiseConfiguration());
        statuses.add(this.initialiseDatabase());
        statuses.add(this.registerCommands());

        return statuses.stream().reduce(true, (acc, curr) -> acc && curr);
    }

    public boolean reloadConfiguration() {
        return this.initialiseConfiguration() && this.loadDatabaseData(true);
    }

    private boolean initialiseConfiguration() {
        // Create the default config file, if it doesn't exist.
        saveDefaultConfig();

        // Load settings from the default config into the plugin settings repository.
        FileConfiguration config = this.getConfig();
        PluginSettingsRepository.maxGlobalDeities = config.getInt("limits.maxDeities", -1);
        PluginSettingsRepository.maxFollowers = config.getInt("limits.maxFollowers", -1);
        PluginSettingsRepository.maxCollectiveFaith = config.getInt("limits.maxCollectiveFaith", 10000);

        PluginSettingsRepository.collectiveAltarReward = config.getInt("rates.collectiveFaith.altarPrayer", 250);
        PluginSettingsRepository.collectiveItemReward = config.getInt("rates.collectiveFaith.itemSacrifice", 350);
        PluginSettingsRepository.collectiveMobReward = config.getInt("rates.collectiveFaith.mobSacrifice", 350);

        PluginSettingsRepository.allowGenderlessDeities = config.getBoolean("deityCreation.allowGenderlessDeities", true);

        ConfigurationSection mysqlSettingsSection = config.getConfigurationSection("mysql");
        DatabaseService.enableMySql = mysqlSettingsSection.getBoolean("enabled", false);
        DatabaseService.mysqlCredentials = new MySQLCredentials(mysqlSettingsSection);

        // Load configured domains.
        ConfigurationSection domainSection = config.getConfigurationSection("domains");
        for (final String domainKey : domainSection.getKeys(false)) {
            final DeityDomain domain = new DeityDomain(domainSection.getConfigurationSection(domainKey), domainKey);
            DeityService.domains.add(domain);
        }

        LoggingService.writeLog(Level.INFO, String.format("Loaded configuration, found %d divine domains.", DeityService.domains.size()));

        return true;
    }

    private boolean initialiseDatabase() {
        // Create the data folder, if SQLite should be used.
        File dataFolder = null;

        if (!DatabaseService.enableMySql) {
            File pluginFolder = getDataFolder();
            dataFolder = new File(pluginFolder, "data");

            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
        }

        final boolean connectionCreated = DatabaseService.CreateConnection(DatabaseService.enableMySql ? null : new File(dataFolder, "database.db").getPath());
        boolean initialisedTables = false;

        if (connectionCreated) {
            DatabaseQuery[] installOrder = new DatabaseQuery[] {
                    DatabaseQuery.CreateDeityTable,
                    DatabaseQuery.CreateUserTable,
                    DatabaseQuery.CreateCollectiveTransactionsTable,
                    DatabaseQuery.CreateDeityCreationSP
            };

            initialisedTables = true;

            for (DatabaseQuery query : installOrder) {
                if (initialisedTables && ((DatabaseService.enableMySql ? query.MySqlQuery : query.SQLiteQuery).length() > 0)) {
                    LoggingService.writeLog(Level.INFO, String.format("Initialising Table: %s", query.name()));
                    try {
                        final PreparedStatement statement = DatabaseService.connection.prepareStatement(
                                DatabaseService.enableMySql
                                ? query.MySqlQuery
                                : query.SQLiteQuery
                        );

                        statement.executeUpdate();
                        statement.close();

                        initialisedTables = initialisedTables && true;
                    } catch (SQLException e) {
                        LoggingService.writeLog(Level.WARNING, String.format("Error initialising table: %s", e.getMessage()));
                        initialisedTables = false;
                    }
                }
            }

            if (initialisedTables) {
                // Log the completion of this phase of startup.
                LoggingService.writeLog(Level.INFO, "Initialised database tables and procedures.");
            } else {
                // Log the failed completion of this phase of startup.
                LoggingService.writeLog(Level.SEVERE, "Could not create required database tables.");
            }
        } else {
            LoggingService.writeLog(Level.SEVERE, "Could not create connection to the database!");
        }

        return connectionCreated && initialisedTables && this.loadDatabaseData(initialisedTables);
    }

    private boolean loadDatabaseData(boolean tablesInitialised) {
        boolean deitiesLoaded = false, usersLoaded = false;

        if (tablesInitialised) {
            // Load deities
            ResultSet deitiesResults = DatabaseService.RunQuery(DatabaseQuery.SelectActiveDeities);

            if (deitiesResults != null) {
                try {
                    while (deitiesResults.next()) {
                        DeityService.deities.put(
                                deitiesResults.getInt(1),
                                new Deity(
                                        deitiesResults.getInt(1),
                                        deitiesResults.getString(2),
                                        deitiesResults.getString(3),
                                        deitiesResults.getString(4),
                                        DeityGender.getGenderById(deitiesResults.getInt(5)),
                                        Material.getMaterial(deitiesResults.getString(6)),
                                        EntityType.fromName(deitiesResults.getString(7)),
                                        PotionEffectType.getByName(deitiesResults.getString(8)),
                                        deitiesResults.getString(9),
                                        deitiesResults.getBoolean(10),
                                        deitiesResults.getBoolean(11),
                                        deitiesResults.getDate(12),
                                        deitiesResults.getString(13),
                                        deitiesResults.getInt(14)
                                )
                        );
                    }

                    deitiesLoaded = true;
                    LoggingService.writeLog(Level.INFO, String.format("Loaded %d deities.", DeityService.deities.size()));
                } catch (SQLException err) {
                    deitiesLoaded = false;
                    LoggingService.writeLog(Level.SEVERE, "Failed to fetch deities data.");
                }
            } else {
                LoggingService.writeLog(Level.SEVERE, "Failed to fetch deities data.");
            }

            // Load users
            ResultSet usersResults = DatabaseService.RunQuery(DatabaseQuery.SelectAllUsers);

            if (usersResults != null) {
                try {
                    while (usersResults.next()) {
                        DeityService.users.put(
                                usersResults.getString(1),
                                new DeityUser(
                                        usersResults.getString(1),
                                        usersResults.getInt(2),
                                        usersResults.getBoolean(3),
                                        usersResults.getBoolean(4),
                                        usersResults.getBoolean(5),
                                        usersResults.getDate(6)
                                )
                        );
                    }

                    usersLoaded = true;
                    LoggingService.writeLog(Level.INFO, String.format("Loaded %d users.", DeityService.users.size()));
                } catch (SQLException err) {
                    usersLoaded = false;
                    LoggingService.writeLog(Level.SEVERE, "Failed to fetch user data.");
                }
            } else {
                LoggingService.writeLog(Level.SEVERE, "Failed to fetch user data.");
            }
        } else {
            LoggingService.writeLog(Level.SEVERE, "Could not fetch user data; tables not initialised.");
        }

        return tablesInitialised && deitiesLoaded && usersLoaded;
    }

    private boolean registerCommands() {
        // Command Executors
        getCommand("deities").setExecutor(new DeityCoreCommandExecutor());

        // Command Tab Completers
        getCommand("deities").setTabCompleter(new DeityCoreCommandTabCompleter());

        // Listeners
        getServer().getPluginManager().registerEvents(new InventoryMenuListener(), this);

        return true;
    }
}
