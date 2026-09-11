package com.projet.repository;

import com.projet.database.DatabaseConnection;
import com.projet.exception.ClientIntrouvableException;
import com.projet.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    public void ajouter(Client client)
            throws SQLException {

        String sql =
                "INSERT INTO client " +
                "(id, nom, prenoms, telephone, email, mot_de_passe) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, client.getId());
            statement.setString(2, client.getNom());
            statement.setString(3, client.getPrenom());
            statement.setString(4, client.getTelephone());
            statement.setString(5, client.getEmail());
            statement.setString(6, client.getMotDePasse());

            statement.executeUpdate();
        }
    }

    public Client rechercher(long id)
            throws SQLException,
                   ClientIntrouvableException {

        String sql =
                "SELECT * FROM client WHERE id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet result =
                         statement.executeQuery()) {

                if (result.next()) {

                    try {
                        return new Client(
                                result.getLong("id"),
                                result.getString("nom"),
                                result.getString("prenoms"),
                                result.getString("telephone"),
                                result.getString("email"),
                                result.getString("mot_de_passe")
                        );

                    } catch (Exception e) {

                        throw new SQLException(
                                "Erreur lors de la création du client.",
                                e
                        );
                    }
                }
            }
        }

        throw new ClientIntrouvableException(
                "Client introuvable : " + id
        );
    }

        public Client authentifier(String email, String motDePasse)
                        throws SQLException,
                                   ClientIntrouvableException {

                String sql =
                                "SELECT * FROM client " +
                                "WHERE email = ? AND mot_de_passe = ?";

                try (Connection connection = DatabaseConnection.getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, email);
                        statement.setString(2, motDePasse);

                        try (ResultSet result = statement.executeQuery()) {
                                if (result.next()) {
                                        try {
                                                return new Client(
                                                                result.getLong("id"),
                                                                result.getString("nom"),
                                                                result.getString("prenoms"),
                                                                result.getString("telephone"),
                                                                result.getString("email"),
                                                                result.getString("mot_de_passe")
                                                );
                                        } catch (Exception e) {
                                                throw new SQLException(
                                                                "Erreur lors de la création du client.", e
                                                );
                                        }
                                }
                        }
                }

                throw new ClientIntrouvableException("Email ou mot de passe incorrect.");
        }

    public List<Client> trouverTous()
            throws SQLException {

        List<Client> clients =
                new ArrayList<Client>();

        String sql = "SELECT * FROM client";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            while (result.next()) {

                try {

                    Client client = new Client(
                            result.getLong("id"),
                            result.getString("nom"),
                            result.getString("prenoms"),
                            result.getString("telephone"),
                            result.getString("email"),
                            result.getString("mot_de_passe")
                    );

                    clients.add(client);

                } catch (Exception e) {

                    throw new SQLException(
                            "Erreur lors de la lecture des clients.",
                            e
                    );
                }
            }
        }

        return clients;
    }
    
}