package com.projet.repository;

import com.projet.database.DatabaseConnection;
import com.projet.model.compte.Transaction;
import com.projet.model.compte.TypeTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    public void ajouter(
            String numeroCompte,
            Transaction transaction)
            throws SQLException {

        String sql =
                "INSERT INTO `transaction` " +
                "(compte_numero, type_transaction, " +
                "montant, description) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    numeroCompte
            );

            statement.setString(
                    2,
                    transaction.getType().name()
            );

            statement.setDouble(
                    3,
                    transaction.getMontant()
            );

            statement.setString(
                    4,
                    transaction.getDescription()
            );

            statement.executeUpdate();
        }
    }

    public List<Transaction> trouverParCompte(
            String numeroCompte)
            throws SQLException {

        List<Transaction> transactions =
                new ArrayList<Transaction>();

        String sql =
                "SELECT * FROM `transaction` " +
                "WHERE compte_numero = ? " +
                "ORDER BY date_transaction ASC";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    numeroCompte
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    Transaction transaction =
                            new Transaction(
                                    result.getLong("id"),
                                    TypeTransaction.valueOf(
                                            result.getString(
                                                    "type_transaction"
                                            )
                                    ),
                                    result.getDouble("montant"),
                                    result.getString(
                                            "description"
                                    )
                            );

                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }
}