package com.projet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/bank_manager";

    private static final String USER =
            "root";

    private static final String PASSWORD =
            "";

    public static Connection getConnection()
            throws SQLException {

        try {
            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Pilote JDBC MySQL introuvable.",
                    e
            );
        }

                Connection connection = DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );

                ensureRoleColumn(connection);
                ensureAdminAccount(connection);
                return connection;
    }

        private static void ensureRoleColumn(Connection connection)
                        throws SQLException {

                try (Statement alter = connection.createStatement()) {
                    alter.executeUpdate(
                            "ALTER TABLE client ADD COLUMN IF NOT EXISTS " +
                            "role VARCHAR(20) NOT NULL DEFAULT 'CLIENT'"
                    );
                }
        }

        private static void ensureAdminAccount(Connection connection)
                        throws SQLException {

                String sql =
                                "INSERT INTO client " +
                                "(id, nom, prenoms, telephone, email, " +
                                "mot_de_passe, role) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE " +
                                "role = VALUES(role), " +
                                "mot_de_passe = VALUES(mot_de_passe)";

                try (PreparedStatement statement =
                                     connection.prepareStatement(sql)) {
                        statement.setLong(1, 900000000001L);
                        statement.setString(2, "Administrateur");
                        statement.setString(3, "BankManager");
                        statement.setString(4, "00000000");
                        statement.setString(5, "admin@bankmanager.local");
                        statement.setString(6, "admin1234");
                        statement.setString(7, "ADMIN");
                        statement.executeUpdate();
                }

                String accountSql =
                                "INSERT INTO comptes " +
                                "(numero_compte, client_id, sold, type_compte, " +
                                "decouvert_autorise, `Taux interet`) " +
                                "SELECT 'ADMIN-00000001', ?, 0.00, 'COURANT', 0.00, 0.0000 " +
                                "WHERE NOT EXISTS " +
                                "(SELECT 1 FROM comptes WHERE numero_compte = 'ADMIN-00000001')";

                try (PreparedStatement statement =
                                     connection.prepareStatement(accountSql)) {
                        statement.setLong(1, 900000000001L);
                        statement.executeUpdate();
                }
        }
}