package com.projet.service;

import java.sql.SQLException;
import java.util.List;

import com.projet.exception.ClientIntrouvableException;
import com.projet.exception.CompteIntrouvableException;
import com.projet.exception.SoldeInsuffisantException;
import com.projet.exception.ValidationException;
import com.projet.model.Client;
import com.projet.model.compte.Compte;
import com.projet.model.compte.Transaction;
import com.projet.model.compte.TypeTransaction;
import com.projet.repository.ClientRepository;
import com.projet.repository.CompteRepository;
import com.projet.repository.TransactionRepository;

public class BankService {

    private ClientRepository clientRepository;
    private CompteRepository compteRepository;
    private TransactionRepository transactionRepository;

    public BankService() {

        clientRepository = new ClientRepository();
        compteRepository = new CompteRepository();
        transactionRepository = new TransactionRepository();
    }

    // =========================
    // CLIENTS
    // =========================

    public void ajouterClient(Client client)
            throws SQLException {

        clientRepository.ajouter(client);
    }

    public Client rechercherClient(long id)
            throws SQLException,
                   ClientIntrouvableException {

        return clientRepository.rechercher(id);
    }

    public List<Client> getClients()
            throws SQLException {

        return clientRepository.trouverTous();
    }

    // =========================
    // COMPTES
    // =========================

    public void ajouterCompte(Compte compte)
            throws SQLException {

        compteRepository.ajouter(compte);
    }

    public Compte rechercherCompte(String numero)
            throws SQLException,
                   CompteIntrouvableException {

        return compteRepository.rechercher(numero);
    }

        public List<Compte> getComptes()
                        throws SQLException {

                return compteRepository.trouverTous();
        }

        public List<Transaction> getHistorique(String numeroCompte)
                        throws SQLException,
                                   CompteIntrouvableException {

                rechercherCompte(numeroCompte);

                return transactionRepository.trouverParCompte(numeroCompte);
        }

    // =========================
    // DEPOT
    // =========================

    public void deposer(
            String numeroCompte,
            double montant)
            throws SQLException,
                   CompteIntrouvableException,
                   ValidationException {

        if (montant <= 0) {
            throw new ValidationException(
                    "Le montant doit être supérieur à zéro."
            );
        }

        Compte compte =
                rechercherCompte(numeroCompte);

        compte.deposer(montant);

        compteRepository.mettreAJourSolde(
                compte.getNumeroCompte(),
                compte.getSolde()
        );

        transactionRepository.ajouter(
                compte.getNumeroCompte(),
                new Transaction(
                        0,
                        TypeTransaction.DEPOT,
                        montant,
                        "Dépôt sur le compte"
                )
        );
    }

    // =========================
    // RETRAIT
    // =========================

    public void retirer(
            String numeroCompte,
            double montant)
            throws SQLException,
                   CompteIntrouvableException,
                   SoldeInsuffisantException,
                   ValidationException {

        if (montant <= 0) {
            throw new ValidationException(
                    "Le montant doit être supérieur à zéro."
            );
        }

        Compte compte =
                rechercherCompte(numeroCompte);

        compte.retirer(montant);

        compteRepository.mettreAJourSolde(
                compte.getNumeroCompte(),
                compte.getSolde()
        );

        transactionRepository.ajouter(
                compte.getNumeroCompte(),
                new Transaction(
                        0,
                        TypeTransaction.RETRAIT,
                        montant,
                        "Retrait sur le compte"
                )
        );
    }

    // =========================
    // VIREMENT
    // =========================

    public void effectuerVirement(
            String numeroSource,
            String numeroDestination,
            double montant)
            throws SQLException,
                   CompteIntrouvableException,
                   SoldeInsuffisantException,
                   ValidationException {

        if (numeroSource.equals(numeroDestination)) {

            throw new ValidationException(
                    "Impossible d'effectuer un virement " +
                    "vers le même compte."
            );
        }

        if (montant <= 0) {

            throw new ValidationException(
                    "Le montant doit être supérieur à zéro."
            );
        }

        Compte source =
                rechercherCompte(numeroSource);

        Compte destination =
                rechercherCompte(numeroDestination);

        source.retirer(montant);
        destination.deposer(montant);

        compteRepository.mettreAJourSolde(
                source.getNumeroCompte(),
                source.getSolde()
        );

        compteRepository.mettreAJourSolde(
                destination.getNumeroCompte(),
                destination.getSolde()
        );

        transactionRepository.ajouter(
                source.getNumeroCompte(),
                new Transaction(
                        0,
                        TypeTransaction.VIREMENT,
                        montant,
                        "Virement vers " + destination.getNumeroCompte()
                )
        );

        transactionRepository.ajouter(
                destination.getNumeroCompte(),
                new Transaction(
                        0,
                        TypeTransaction.VIREMENT,
                        montant,
                        "Virement depuis " + source.getNumeroCompte()
                )
        );
    }
}