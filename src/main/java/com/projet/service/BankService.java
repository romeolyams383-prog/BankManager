package com.projet.service;

import com.projet.exception.ClientIntrouvableException;
import com.projet.exception.CompteIntrouvableException;
import com.projet.exception.SoldeInsuffisantException;
import com.projet.exception.ValidationException;
import com.projet.model.Client;
import com.projet.model.compte.Compte;
import com.projet.repository.ClientRepository;
import com.projet.repository.CompteRepository;
import com.projet.repository.TransactionRepository;

import java.sql.SQLException;
import java.util.List;

public class BankService {

    private ClientRepository clientRepository;
    private CompteRepository compteRepository;
    private TransactionRepository transactionRepository;

    public BankService() {

        clientRepository =
                new ClientRepository();

        compteRepository =
                new CompteRepository();

        transactionRepository =
                new TransactionRepository();
    }

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

    public void ajouterCompte(Compte compte)
            throws SQLException {

        compteRepository.ajouter(compte);
    }

    public Compte rechercherCompte(String numero)
            throws SQLException,
                   CompteIntrouvableException {

        return compteRepository.rechercher(numero);
    }

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
    }
}