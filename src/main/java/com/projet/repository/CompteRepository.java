package com.projet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.projet.database.DatabaseConnection;
import com.projet.exception.CompteIntrouvableException;
import com.projet.model.Client;
import com.projet.model.compte.Compte;
import com.projet.model.compte.CompteCourant;
import com.projet.model.compte.CompteEpargne;

public class CompteRepository {

    public void ajouter(Compte compte)
            throws SQLException {

        String sql =
                "INSERT INTO comptes " +
                "(numero_compte, client_id, sold, " +
                "type_compte, decouvert_autorise, `Taux interet`) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    compte.getNumeroCompte()
            );

            statement.setLong(
                    2,
                    compte.getClient().getId()
            );

            statement.setDouble(
                    3,
                    compte.getSolde()
            );

            if (compte instanceof CompteCourant) {

                statement.setString(
                        4,
                        "COURANT"
                );

                statement.setDouble(5, 0.0);

                statement.setDouble(
                        6,
                        0.0
                );

            } else {

                CompteEpargne epargne =
                        (CompteEpargne) compte;

                statement.setString(
                        4,
                        "EPARGNE"
                );

                statement.setDouble(
                        5,
                        0.0
                );

                statement.setDouble(
                        6,
                        epargne.getTauxInteret()
                );
            }

            statement.executeUpdate();
        }
    }

    public Compte rechercher(String numero)
            throws SQLException,
                   CompteIntrouvableException {

        String sql =
                "SELECT c.*, " +
                "cl.nom, cl.prenoms, cl.telephone, " +
                "cl.email, cl.mot_de_passe " +
                "FROM comptes c " +
                "JOIN client cl ON c.client_id = cl.id " +
                "WHERE c.numero_compte = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, numero);

            try (ResultSet result =
                         statement.executeQuery()) {

                if (result.next()) {

                    try {

                        Client client = new Client(
                                result.getLong("client_id"),
                                result.getString("nom"),
                                result.getString("prenoms"),
                                result.getString("telephone"),
                                result.getString("email"),
                                result.getString("mot_de_passe")
                        );

                        String type =
                                result.getString("type_compte");

                                                Compte compte;

                                                if ("COURANT".equals(type)) {

                                                        compte = new CompteCourant(
                                    result.getString("numero_compte"),
                                    client,
                                    result.getDouble(
                                            "decouvert_autorise"
                                    )
                            );

                        } else {

                            compte = new CompteEpargne(
                                    result.getString("numero_compte"),
                                    client,
                                    result.getDouble(
                                            "Taux interet"
                                    )
                            );
                        }

                                                compte.setSolde(result.getDouble("sold"));
                                                return compte;

                    } catch (Exception e) {

                        throw new SQLException(
                                "Erreur lors de la reconstruction du compte.",
                                e
                        );
                    }
                }
            }
        }

        throw new CompteIntrouvableException(
                "Compte introuvable : " + numero
        );
    }
    // create a method to update the balance of a Compte in the database
    public void mettreAJourSolde(
        String numeroCompte,
        double nouveauSolde)
        throws SQLException {

    String sql =
            "UPDATE comptes SET sold = ? " +
            "WHERE numero_compte = ?";

    try (Connection connection =
                 DatabaseConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setDouble(1, nouveauSolde);
        statement.setString(2, numeroCompte);

        statement.executeUpdate();
    }
}
}