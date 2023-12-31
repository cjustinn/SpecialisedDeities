package io.github.cjustinn.specialiseddeities.services;

import io.github.cjustinn.specialiseddeities.enums.modelfields.BaseTableField;
import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQuery;
import io.github.cjustinn.specialiseddeities.models.SQL.DatabaseQueryValue;
import io.github.cjustinn.specialiseddeities.models.SQL.MySQLCredentials;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DatabaseService {
    public static boolean enableMySql = false;

    public static @Nullable MySQLCredentials mysqlCredentials = null;
    public static Connection connection = null;

    public static boolean CreateConnection(String path) {
        try {
            Class.forName("org.sqlite.JDBC");
            if (connection == null) {
                if (enableMySql) {
                    connection = DriverManager.getConnection(mysqlCredentials.getConnectionString(), mysqlCredentials.username, mysqlCredentials.password);
                } else {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                }

                return true;
            }

            return false;
        } catch (ClassNotFoundException | SQLException err) {
            LoggingService.writeLog(Level.SEVERE, String.format("Failed to create database connection: %s", err.getMessage()));
            return false;
        }
    }

    public static boolean CloseConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;

                return true;
            } catch (SQLException err) {
                return false;
            }
        }

        return false;
    }

    public static @Nullable ResultSet RunQuery(DatabaseQuery query) {
        try {
            PreparedStatement statement = connection.prepareStatement(enableMySql ? query.MySqlQuery : query.SQLiteQuery);
            return statement.executeQuery();
        } catch (SQLException e) {
            LoggingService.writeLog(Level.SEVERE, String.format(
                    "Error occurred running query [%s]: %s",
                    query.name(),
                    e.getMessage()
            ));

            return null;
        }
    }

    public static @Nullable ResultSet RunQuery(DatabaseQuery query, DatabaseQueryValue[] values) {
        try {
            PreparedStatement statement = connection.prepareStatement(enableMySql ? query.MySqlQuery : query.SQLiteQuery);

            for (DatabaseQueryValue queryValue : values) {
                switch (queryValue.type) {
                    case Integer:
                        statement.setInt(queryValue.position, (int) queryValue.value);
                        break;
                    case Double:
                        statement.setDouble(queryValue.position, (double) queryValue.value);
                        break;
                    case Boolean:
                        statement.setBoolean(queryValue.position, (boolean) queryValue.value);
                        break;
                    case Date:
                        statement.setDate(queryValue.position, (Date) queryValue.value);
                        break;
                    default:
                        statement.setString(queryValue.position, (String) queryValue.value);
                        break;
                }
            }

            return statement.executeQuery();
        } catch (SQLException e) {
            LoggingService.writeLog(Level.SEVERE, String.format(
                    "Error occurred running query [%s]: %s",
                    query.name(),
                    e.getMessage()
            ));

            return null;
        }
    }

    public static boolean RunUpdate(DatabaseQuery query) {
        try {
            PreparedStatement statement = connection.prepareStatement(enableMySql ? query.MySqlQuery : query.SQLiteQuery);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            LoggingService.writeLog(Level.SEVERE, String.format(
                    "Error occurred running query [%s]: %s",
                    query.name(),
                    e.getMessage()
            ));

            return false;
        }
    }

    public static boolean RunUpdate(DatabaseQuery query, DatabaseQueryValue[] values) {
        try {
            PreparedStatement statement = connection.prepareStatement(enableMySql ? query.MySqlQuery : query.SQLiteQuery);

            for (DatabaseQueryValue queryValue : values) {
                switch (queryValue.type) {
                    case Integer:
                        statement.setInt(queryValue.position, (int) queryValue.value);
                        break;
                    case Double:
                        statement.setDouble(queryValue.position, (double) queryValue.value);
                        break;
                    case Boolean:
                        statement.setBoolean(queryValue.position, (boolean) queryValue.value);
                        break;
                    case Date:
                        statement.setDate(queryValue.position, (Date) queryValue.value);
                        break;
                    default:
                        statement.setString(queryValue.position, (String) queryValue.value);
                        break;
                }
            }

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            LoggingService.writeLog(Level.SEVERE, String.format(
                    "Error occurred running query [%s]: %s",
                    query.name(),
                    e.getMessage()
            ));

            return false;
        }
    }

    public static boolean RunUpdate(DatabaseQuery query, DatabaseQueryValue[] values, final Map<? extends BaseTableField, DatabaseQueryValue> fields) {
        final String queryFields = fields.entrySet().stream().map(entry -> String.format("%s = ?", entry.getKey().getColumn())).collect(Collectors.joining(", "));
        final String queryString = (enableMySql ? query.MySqlQuery : query.SQLiteQuery).replace("%fields%", queryFields);

        try {
            PreparedStatement statement = connection.prepareStatement(queryString);

            // Place the field values into the statement.
            for (DatabaseQueryValue fieldValue : fields.values()) {
                switch (fieldValue.type) {
                    case Integer:
                        statement.setInt(fieldValue.position, (int) fieldValue.value);
                        break;
                    case Double:
                        statement.setDouble(fieldValue.position, (double) fieldValue.value);
                        break;
                    case Boolean:
                        statement.setBoolean(fieldValue.position, (boolean) fieldValue.value);
                        break;
                    case Date:
                        statement.setDate(fieldValue.position, (Date) fieldValue.value);
                        break;
                    default:
                        statement.setString(fieldValue.position, (String) fieldValue.value);
                        break;
                }
            }

            // Place the regular values into the statement.
            for (DatabaseQueryValue queryValue : values) {
                switch (queryValue.type) {
                    case Integer:
                        statement.setInt(queryValue.position, (int) queryValue.value);
                        break;
                    case Double:
                        statement.setDouble(queryValue.position, (double) queryValue.value);
                        break;
                    case Boolean:
                        statement.setBoolean(queryValue.position, (boolean) queryValue.value);
                        break;
                    case Date:
                        statement.setDate(queryValue.position, (Date) queryValue.value);
                        break;
                    default:
                        statement.setString(queryValue.position, (String) queryValue.value);
                        break;
                }
            }

            // Execute the statement.
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            LoggingService.writeLog(Level.SEVERE, String.format(
                    "Error occurred running query [%s]: %s",
                    query.name(),
                    e.getMessage()
            ));

            return false;
        }
    }
}
