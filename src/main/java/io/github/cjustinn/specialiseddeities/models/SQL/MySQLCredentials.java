package io.github.cjustinn.specialiseddeities.models.SQL;

import org.bukkit.configuration.ConfigurationSection;

public class MySQLCredentials {
    public final String host;
    public final String port;
    public final String database;
    public final String username;
    public final String password;

    public MySQLCredentials(ConfigurationSection section) {
        this.host = section.getString("host");
        this.port = section.getString("port");
        this.database = section.getString("database");
        this.username = section.getString("user");
        this.password = section.getString("password");
    }

    public String getConnectionString() {
        return String.format("jdbc:mysql://%s:%s/%s", this.host, this.port, this.database);
    }
}
