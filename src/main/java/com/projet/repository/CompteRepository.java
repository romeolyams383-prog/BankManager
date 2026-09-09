package com.projet.repository;

import com.projet.database.DatabaseConnection;
import com.projet.exception.CompteIntrouvableException;
import com.projet.model.Client;
import com.projet.model.compte.Compte;
import com.projet.model.compte.CompteCourant;
import com.projet.model.compte.CompteEpargne;

import java.sql.*;

public class CompteRepository {

    public void ajouter(Compte compte)
            throws SQLException {

        String sql =
                "INSERT INTO comptes " +
                "(numero_compte, client_id, solde, " +
                "type_compte, decouvert_autorise, taux_interet) " +
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

                CompteCourant courant =
                        (CompteCourant) compte;

                statement.setString(
                        4,
                        "COURANT"
                );

                statement.setDouble(
                        5,
                        courant.getDecouvertAutorise()
                );

                statement.setNull(
                        6,
                        Types.DECIMAL
                );

            } else {

                CompteEpargne epargne =
                        (CompteEpargne) compte;

                statement.setString(
                        4,
                        "EPARGNE"
                );

                statement.setNull(
                        5,
                        Types.DECIMAL
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
                "cl.nom, cl.prenom, cl.telephone, " +
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
                                result.getString("prenom"),
                                result.getString("telephone"),
                                result.getString("email"),
                                result.getString("mot_de_passe")
                        );

                        String type =
                                result.getString("type_compte");

                        if ("COURANT".equals(type)) {

                            return new CompteCourant(
                                    result.getString("numero_compte"),
                                    client,
                                    result.getDouble(
                                            "decouvert_autorise"
                                    )
                            );

                        } else {

                            return new CompteEpargne(
                                    result.getString("numero_compte"),
                                    client,
                                    result.getDouble(
                                            "taux_interet"
                                    )
                            );
                        }

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
            "UPDATE comptes SET solde = ? " +
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