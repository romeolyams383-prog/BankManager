package com.projet.ui;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.projet.model.Client;
import com.projet.model.compte.Compte;
import com.projet.model.compte.CompteCourant;
import com.projet.model.compte.CompteEpargne;
import com.projet.model.compte.Transaction;
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
                                                                                menuComptes();
                    break;

                case 2:
                                                                                menuOperations();
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
                "1. Gestion des comptes"
        );
        System.out.println(
                "2. Opérations bancaires"
        );
        System.out.println(
                "0. Quitter"
        );
        System.out.println(
                "===================================="
        );
    }

        private void menuComptes() {

                int choix;

                do {
                        System.out.println();
                        System.out.println("===== GESTION DES COMPTES =====");
                        System.out.println("1. Créer un compte");
                        System.out.println("2. Consulter un compte");
                        System.out.println("0. Retour");

                        choix = lireEntier("Votre choix : ");

                        switch (choix) {
                                case 1:
                                        creerCompte();
                                        break;
                                case 2:
                                        consulterCompte();
                                        break;
                                case 0:
                                        break;
                                default:
                                        System.out.println("Choix invalide.");
                        }
                } while (choix != 0);
        }

        private void menuOperations() {

                int choix;

                do {
                        System.out.println();
                        System.out.println("===== OPERATIONS BANCAIRES =====");
                        System.out.println("1. Déposer de l'argent");
                        System.out.println("2. Retirer de l'argent");
                        System.out.println("3. Effectuer un virement");
                        System.out.println("0. Retour");

                        choix = lireEntier("Votre choix : ");

                        switch (choix) {
                                case 1:
                                        deposer();
                                        break;
                                case 2:
                                        retirer();
                                        break;
                                case 3:
                                        effectuerVirement();
                                        break;
                                case 0:
                                        break;
                                default:
                                        System.out.println("Choix invalide.");
                        }
                } while (choix != 0);
        }

    private void creerCompte() {

        try {

            String nom = lireTexte("Nom : ");
            String prenom = lireTexte("Prénom : ");
            String telephone = lireTexte("Numéro de téléphone : ");
            String email = lireTexte("Mail : ");
            String motDePasse = lireTexte("Mot de passe : ");

            Client client = new Client(
                    System.currentTimeMillis(),
                    nom,
                    prenom,
                    telephone,
                    email,
                    motDePasse
            );

            String numero = genererNumeroCompte();

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

                compte =
                        new CompteCourant(
                                numero,
                                client
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

                        banque.ajouterClient(client);
            banque.ajouterCompte(compte);

            System.out.println(
                    "Compte créé avec succès."
            );
            System.out.println(
                    "Numéro du compte : " + numero
            );

        } catch (Exception e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );
        }
    }

        private String genererNumeroCompte() {

                return "CM-" + UUID.randomUUID()
                                .toString()
                                .replace("-", "")
                                .substring(0, 12)
                                .toUpperCase();
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

                        menuCompteConsulte(compte);

        } catch (Exception e) {

            System.out.println(
                    "Erreur : " + e.getMessage()
            );
        }
    }

        private void menuCompteConsulte(Compte compte) {

                int choix;

                do {
                        System.out.println();
                        System.out.println("===== ACTIONS DU COMPTE =====");
                        System.out.println("1. Effectuer un transfert");
                        System.out.println("2. Voir l'historique");
                        System.out.println("0. Retour");

                        choix = lireEntier("Votre choix : ");

                        switch (choix) {
                                case 1:
                                        effectuerTransfertDepuis(compte.getNumeroCompte());
                                        break;
                                case 2:
                                        afficherHistorique(compte.getNumeroCompte());
                                        break;
                                case 0:
                                        break;
                                default:
                                        System.out.println("Choix invalide.");
                        }
                } while (choix != 0);
        }

        private void effectuerTransfertDepuis(String numeroSource) {

                try {
                        String numeroDestination = lireTexte(
                                        "Numéro du compte destinataire : "
                        );
                        double montant = lireDouble("Montant à transférer : ");

                        banque.effectuerVirement(
                                        numeroSource,
                                        numeroDestination,
                                        montant
                        );

                        System.out.println("Transfert effectué avec succès.");
                        System.out.println(
                                        "Nouveau solde : "
                                        + banque.rechercherCompte(numeroSource).getSolde()
                        );
                } catch (Exception e) {
                        System.out.println("Erreur : " + e.getMessage());
                }
        }

        private void afficherHistorique(String numeroCompte) {

                try {
                        List<Transaction> transactions =
                                        banque.getHistorique(numeroCompte);

                        System.out.println();
                        System.out.println("===== HISTORIQUE DU COMPTE =====");

                        if (transactions.isEmpty()) {
                                System.out.println("Aucune transaction enregistrée.");
                                return;
                        }

                        for (Transaction transaction : transactions) {
                                System.out.println(transaction);
                        }
                } catch (Exception e) {
                        System.out.println("Erreur : " + e.getMessage());
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

            banque.deposer(numero, montant);

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

            banque.retirer(numero, montant);

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