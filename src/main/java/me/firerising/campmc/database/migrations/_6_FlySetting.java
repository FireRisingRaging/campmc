package me.firerising.campmc.database.migrations;

import com.songoda.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _6_FlySetting extends DataMigration {

    public _6_FlySetting() {
        super(6);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {

        // Create permissions table
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + tablePrefix + "settings ADD COLUMN fly TINYINT NOT NULL DEFAULT 0");
        }
    }

}
