package com.projet;

import java.util.List;

import com.projet.exception.SoldeInsuffisantException;
import com.projet.model.Client;
import com.projet.model.compte.CompteCourant;
import com.projet.model.compte.Transaction;
import com.projet.model.compte.TypeTransaction;
import com.projet.repository.TransactionRepository;
import com.projet.service.BankService;

import junit.framework.TestCase;

public class BankServiceTest extends TestCase {

    public void testOperationsBancaires()
            throws Exception {

        long identifiant = System.currentTimeMillis();
        String numeroSource = "BS" + identifiant;
        String numeroDestination = "BD" + identifiant;

        Client client = new Client(
                identifiant,
                "Test",
                "Service",
                "97000000",
                "service.test." + identifiant + "@gmail.com",
                "123456"
        );

        BankService service = new BankService();
        service.ajouterClient(client);

        service.ajouterCompte(
                new CompteCourant(numeroSource, client, 0.0)
        );
        service.ajouterCompte(
                new CompteCourant(numeroDestination, client, 0.0)
        );

        service.deposer(numeroSource, 1000.0);
        assertEquals(1000.0,
                service.rechercherCompte(numeroSource).getSolde());

        service.retirer(numeroSource, 200.0);
        assertEquals(800.0,
                service.rechercherCompte(numeroSource).getSolde());

        try {
            service.retirer(numeroSource, 1000.0);
            fail("Un retrait supérieur au solde devait échouer.");
        } catch (SoldeInsuffisantException e) {
            // Comportement attendu.
        }
        assertEquals(800.0,
                service.rechercherCompte(numeroSource).getSolde());

        service.deposer(numeroDestination, 300.0);
        service.effectuerVirement(
                numeroSource,
                numeroDestination,
                250.0
        );

        assertEquals(550.0,
                service.rechercherCompte(numeroSource).getSolde());
        assertEquals(550.0,
                service.rechercherCompte(numeroDestination).getSolde());

        try {
            service.effectuerVirement(
                    numeroSource,
                    numeroDestination,
                    1000.0
            );
            fail("Un virement supérieur au solde devait échouer.");
        } catch (SoldeInsuffisantException e) {
            // Comportement attendu.
        }

        assertEquals(550.0,
                service.rechercherCompte(numeroSource).getSolde());
        assertEquals(550.0,
                service.rechercherCompte(numeroDestination).getSolde());

        TransactionRepository transactionRepository =
                new TransactionRepository();
        List<Transaction> transactionsSource =
                transactionRepository.trouverParCompte(numeroSource);
        List<Transaction> transactionsDestination =
                transactionRepository.trouverParCompte(
                        numeroDestination
                );

        assertEquals(3, transactionsSource.size());
        assertEquals(TypeTransaction.DEPOT,
                transactionsSource.get(0).getType());
        assertEquals(TypeTransaction.RETRAIT,
                transactionsSource.get(1).getType());
        assertEquals(TypeTransaction.VIREMENT,
                transactionsSource.get(2).getType());

        assertEquals(2, transactionsDestination.size());
        assertEquals(TypeTransaction.DEPOT,
                transactionsDestination.get(0).getType());
        assertEquals(TypeTransaction.VIREMENT,
                transactionsDestination.get(1).getType());
    }
}
