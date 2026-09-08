package com.projet;

import java.sql.Connection;

import com.projet.database.DatabaseConnection;
import com.projet.service.BankService;
import com.projet.ui.Menu;

public class Main {

    public static void main(String[] args) {

        try {

            Connection connection =
                    DatabaseConnection.getConnection();

            System.out.println(
                    "Connexion à MySQL réussie !"
            );

            connection.close();

            BankService banque =
                    new BankService();

            Menu menu =
                    new Menu(banque);

            menu.demarrer();

        } catch (Exception e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );
        }
    }
}