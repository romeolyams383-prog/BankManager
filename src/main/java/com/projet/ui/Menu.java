package com.projet.ui;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.projet.exception.ValidationException;
import com.projet.model.Client;
import com.projet.model.compte.Compte;
import com.projet.model.compte.CompteCourant;
import com.projet.model.compte.CompteEpargne;
import com.projet.service.BankService;

public class Menu {

    private Scanner scanner;
    private BankService banque;

    public Menu(BankService banque) {

        this.banque = banque;
        this.scanner = new Scanner(System.in);
    }

    public void demarrer() {

        int choix;

        do {

            afficherMenu();

            choix = lireEntier("Votre choix : ");

            switch (choix) {

                case 1:
                    creerClient();
                    break;

                case 2:
                    creerCompte();
                    break;

                case 3:
                    consulterCompte();
                    break;

                case 4:
                    deposer();
                    break;

                case 5:
                    retirer();
                    break;

                case 6:
                    effectuerVirement();
                    break;

                case 7:
                    afficherClients();
                    break;

                case 0:
                    System.out.println(
                            "Fermeture de BankManager..."
                    );
                    break;

                default:
                    System.out.println(
                            "Choix invalide."
                    );
            }

        } while (choix != 0);

        scanner.close();
    }

    private void afficherMenu() {

        System.out.println();
        System.out.println(
                "===================================="
        );
        System.out.println(
                "          BANK MANAGER"
        );
        System.out.println(
                "===================================="
        );
        System.out.println(
                "1. Créer un client"
        );
        System.out.println(
                "2. Créer un compte"
        );
        System.out.println(
                "3. Consulter un compte"
        );
        System.out.println(
                "4. Déposer de l'argent"
        );
        System.out.println(
                "5. Retirer de l'argent"
        );
        System.out.println(
                "6. Effectuer un virement"
        );
        System.out.println(
                "7. Afficher les clients"
        );
        System.out.println(
                "0. Quitter"
        );
        System.out.println(
                "===================================="
        );
    }

    private void creerClient() {

        try {

            long id = lireLong("ID : ");
            String nom = lireTexte("Nom : ");
            String prenom = lireTexte("Prénom : ");
            String telephone =
                    lireTexte("Téléphone : ");
            String email =
                    lireTexte("Email : ");
            String motDePasse =
                    lireTexte("Mot de passe : ");

            Client client = new Client(
                    id,
                    nom,
                    prenom,
                    telephone,
                    email,
                    motDePasse
            );

            banque.ajouterClient(client);

            System.out.println(
                    "Client créé avec succès."
            );

        } catch (ValidationException e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );

        } catch (SQLException e) {

            System.out.println(
                    "Erreur base de données : "
                    + e.getMessage()
            );
        }
    }

    private void creerCompte() {

        try {

            long idClient =
                    lireLong("ID du client : ");

            Client client =
                    banque.rechercherClient(idClient);

            String numero =
                    lireTexte(
                            "Numéro du compte : "
                    );

            System.out.println(
                    "1. Compte courant"
            );

            System.out.println(
                    "2. Compte épargne"
            );

            int type =
                    lireEntier("Type : ");

            Compte compte;

            if (type == 1) {

                double decouvert =
                        lireDouble(
                                "Découvert autorisé : "
                        );

                compte =
                        new CompteCourant(
                                numero,
                                client,
                                decouvert
                        );

            } else if (type == 2) {

                double taux =
                        lireDouble(
                                "Taux d'intérêt : "
                        );

                compte =
                        new CompteEpargne(
                                numero,
                                client,
                                taux
                        );

            } else {

                System.out.println(
                        "Type invalide."
                );

                return;
            }

            banque.ajouterCompte(compte);

            System.out.println(
                    "Compte créé avec succès."
            );

        } catch (Exception e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );
        }
    }

    private void consulterCompte() {

        try {

            String numero =
                    lireTexte(
                            "Numéro du compte : "
                    );

            Compte compte =
                    banque.rechercherCompte(numero);

            System.out.println();
            System.out.println(
                    "===== COMPTE ====="
            );

            System.out.println(
                    "Numéro : "
                    + compte.getNumeroCompte()
            );

            System.out.println(
                    "Client : "
                    + compte.getClient().getPrenom()
                    + " "
                    + compte.getClient().getNom()
            );

            System.out.println(
                    "Solde : "
                    + compte.getSolde()
            );

            compte.afficherTypeCompte();

        } catch (Exception e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );
        }
    }

    private void deposer() {

        try {

            String numero =
                    lireTexte(
                            "Numéro du compte : "
                    );

            double montant =
                    lireDouble(
                            "Montant : "
                    );

            Compte compte =
                    banque.rechercherCompte(numero);

            compte.deposer(montant);

            System.out.println(
                    "Dépôt effectué."
            );

        } catch (Exception e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );
        }
    }

    private void retirer() {

        try {

            String numero =
                    lireTexte(
                            "Numéro du compte : "
                    );

            double montant =
                    lireDouble(
                            "Montant : "
                    );

            Compte compte =
                    banque.rechercherCompte(numero);

            compte.retirer(montant);

            System.out.println(
                    "Retrait effectué."
            );

        } catch (Exception e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );
        }
    }

    private void effectuerVirement() {

        try {

            String source =
                    lireTexte(
                            "Compte source : "
                    );

            String destination =
                    lireTexte(
                            "Compte destination : "
                    );

            double montant =
                    lireDouble(
                            "Montant : "
                    );

            banque.effectuerVirement(
                    source,
                    destination,
                    montant
            );

            System.out.println(
                    "Virement effectué avec succès."
            );

        } catch (Exception e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );
        }
    }

    private void afficherClients() {

        try {

            List<Client> clients =
                    banque.getClients();

            System.out.println();
            System.out.println(
                    "===== CLIENTS ====="
            );

            for (Client client : clients) {

                System.out.println(
                        client
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );
        }
    }

    private String lireTexte(String message) {

        System.out.print(message);

        return scanner.nextLine();
    }

    private int lireEntier(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Integer.parseInt(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Entrez un nombre entier."
                );
            }
        }
    }

    private long lireLong(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Long.parseLong(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Entrez un nombre valide."
                );
            }
        }
    }

    private double lireDouble(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Double.parseDouble(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Entrez un nombre valide."
                );
            }
        }
    }
}