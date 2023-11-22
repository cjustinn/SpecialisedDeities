package io.github.cjustinn.specialiseddeities.enums.queries;

public enum DatabaseQuery {
    CreateDeityTable(
            "CREATE TABLE IF NOT EXISTS sd_deities (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(150) NOT NULL, title_override VARCHAR(250) DEFAULT NULL, domain TEXT NOT NULL, gender INT NOT NULL, sacrifice_item TEXT NOT NULL, sacrifice_mob TEXT NOT NULL, status_effect TEXT NOT NULL, creator TEXT NOT NULL, active BIT DEFAULT 1, protected BIT DEFAULT 0, created DATETIME DEFAULT CURRENT_TIMESTAMP);",
            "CREATE TABLE IF NOT EXISTS sd_deities (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(150) NOT NULL, title_override VARCHAR(250) DEFAULT NULL, domain TEXT NOT NULL, gender INT NOT NULL, sacrifice_item TEXT NOT NULL, sacrifice_mob TEXT NOT NULL, status_effect TEXT NOT NULL, creator TEXT NOT NULL, active BIT DEFAULT 1, protected BIT DEFAULT 0, created DATETIME DEFAULT CURRENT_TIMESTAMP);"
    ),
    CreateUserTable(
            "CREATE TABLE IF NOT EXISTS sd_users (uuid VARCHAR(36) NOT NULL PRIMARY KEY, patron INT NOT NULL, is_leader BIT NOT NULL DEFAULT 0, is_demigod BIT NOT NULL DEFAULT 0, is_god BIT NOT NULL DEFAULT 0, pledged DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (patron) REFERENCES sd_deities(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS sd_users (uuid VARCHAR(36) NOT NULL PRIMARY KEY, patron INT NOT NULL, is_leader BIT NOT NULL DEFAULT 0, is_demigod BIT NOT NULL DEFAULT 0, is_god BIT NOT NULL DEFAULT 0, pledged DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (patron) REFERENCES sd_deities(id) ON DELETE CASCADE);"
    ),
    CreateCollectiveTransactionsTable(
            "CREATE TABLE IF NOT EXISTS sd_cpoint_transactions (id INT AUTO_INCREMENT PRIMARY KEY, player VARCHAR(36) NOT NULL, deity INT NOT NULL, reason VARCHAR(250) NOT NULL, amount INT NOT NULL, FOREIGN KEY (deity) REFERENCES sd_deities(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS sd_cpoint_transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, player VARCHAR(36) NOT NULL, deity INT NOT NULL, reason VARCHAR(250) NOT NULL, amount INT NOT NULL, FOREIGN KEY (deity) REFERENCES sd_deities(id) ON DELETE CASCADE);"
    ),
    CreateDeityCreationSP(
            "CREATE PROCEDURE IF NOT EXISTS sd_create_deity (IN deity_name VARCHAR(150), IN deity_title VARCHAR(250), IN deity_domain TEXT, IN deity_gender INT, IN deity_item TEXT, IN deity_mob TEXT, IN deity_status_effect TEXT, IN deity_protected BIT, IN deity_creator TEXT, IN user_is_deity_leader BIT, IN user_is_god BIT) BEGIN INSERT INTO sd_deities (name,  title_override,  domain,  gender,  sacrifice_item,  sacrifice_mob,  status_effect,  protected,  creator) VALUES (deity_name, deity_title, deity_domain, deity_gender, deity_item, deity_mob, deity_status_effect, deity_protected, deity_creator); IF (deity_creator <> 'server') THEN INSERT INTO sd_users (uuid,  patron,  is_leader,  is_god) VALUES (deity_creator,  (SELECT id FROM sd_deities WHERE id = LAST_INSERT_ID() LIMIT 1),  user_is_deity_leader,  user_is_god); END IF; SELECT id, name, title_override, domain, gender, sacrifice_item, sacrifice_mob, status_effect, creator, active, protected, created, (SELECT uuid FROM sd_users WHERE patron = d.id AND is_leader = 1 LIMIT 1) as leader, COALESCE((SELECT SUM(amount) FROM sd_cpoint_transactions WHERE deity = d.id GROUP BY deity),  0) as collective_points FROM sd_deities d WHERE id = LAST_INSERT_ID(); END;",
            ""
    ),
    SelectAllUsers(
            "SELECT uuid, patron, is_leader, is_demigod, is_god, pledged FROM sd_users;",
            "SELECT uuid, patron, is_leader, is_demigod, is_god, pledged FROM sd_users;"
    ),
    SelectUserById(
            "SELECT uuid, patron, is_leader, is_demigod, is_god, pledged FROM sd_users WHERE uuid = ? LIMIT 1;",
            "SELECT uuid, patron, is_leader, is_demigod, is_god, pledged FROM sd_users WHERE uuid = ? LIMIT 1;"
    ),
    SelectActiveDeities(
            "SELECT id, name, title_override, domain, gender, sacrifice_item, sacrifice_mob, status_effect, creator, active, protected, created, (SELECT uuid FROM sd_users WHERE patron = d.id AND is_leader = 1 LIMIT 1) as leader, COALESCE((SELECT SUM(amount) FROM sd_cpoint_transactions WHERE deity = d.id GROUP BY deity), 0) as collective_points FROM sd_deities d WHERE active = 1;",
            "SELECT id, name, title_override, domain, gender, sacrifice_item, sacrifice_mob, status_effect, creator, active, protected, created, (SELECT uuid FROM sd_users WHERE patron = d.id AND is_leader = 1 LIMIT 1) as leader, COALESCE((SELECT SUM(amount) FROM sd_cpoint_transactions WHERE deity = d.id GROUP BY deity), 0) as collective_points FROM sd_deities d WHERE active = 1;"
    ),
    SelectLatestDeity(
            "SELECT id, name, title_override, domain, gender, sacrifice_item, sacrifice_mob, status_effect, creator, active, protected, created, (SELECT uuid FROM sd_users WHERE patron = d.id AND is_leader = 1 LIMIT 1) as leader, COALESCE((SELECT SUM(amount) FROM sd_cpoint_transactions WHERE deity = d.id GROUP BY deity), 0) as collective_points FROM sd_deities d WHERE id = LAST_INSERT_ID();",
            "SELECT id, name, title_override, domain, gender, sacrifice_item, sacrifice_mob, status_effect, creator, active, protected, created, (SELECT uuid FROM sd_users WHERE patron = d.id AND is_leader = 1 LIMIT 1) as leader, COALESCE((SELECT SUM(amount) FROM sd_cpoint_transactions WHERE deity = d.id GROUP BY deity), 0) as collective_points FROM sd_deities d WHERE id = last_insert_rowid();"
    ),
    CreateDeity(
            "CALL sd_create_deity(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
            ""
    ),
    InsertDeity(
            "INSERT INTO sd_deities (name, title_override, domain, gender, sacrifice_item, sacrifice_mob, status_effect, protected, creator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",
            "INSERT INTO sd_deities (name, title_override, domain, gender, sacrifice_item, sacrifice_mob, status_effect, protected, creator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
    ),
    CreateDeityInsertUser(
            "INSERT INTO sd_users (uuid, patron, is_leader, is_god) VALUES (?, (SELECT id FROM sd_deities WHERE id = LAST_INSERT_ID() LIMIT 1), ?, ?);",
            "INSERT INTO sd_users (uuid, patron, is_leader, is_god) VALUES (?, (SELECT id FROM sd_deities WHERE id = last_insert_rowid() LIMIT 1), ?, ?);"
    );

    public final String MySqlQuery;
    public final String SQLiteQuery;

    DatabaseQuery(String mysql, String sqlite) {
        this.MySqlQuery = mysql;
        this.SQLiteQuery = sqlite;
    }
}
