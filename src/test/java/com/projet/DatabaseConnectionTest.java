package com.projet;
import java.sql.Connection;

import com.projet.database.DatabaseConnection;

import junit.framework.TestCase;

public class DatabaseConnectionTest extends TestCase {

    public void testConnexion() throws Exception {

    try (Connection connection = DatabaseConnection.getConnection()) {
        assertNotNull(connection);
        assertFalse(connection.isClosed());
    }

        System.out.println(
                "Connexion MySQL réussie !"
        );
    }
}
    

