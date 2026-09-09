package com.projet;

import java.util.List;

import com.projet.model.Client;
import com.projet.model.compte.CompteCourant;
import com.projet.model.compte.Transaction;
import com.projet.model.compte.TypeTransaction;
import com.projet.repository.ClientRepository;
import com.projet.repository.CompteRepository;
import com.projet.repository.TransactionRepository;

import junit.framework.TestCase;

public class TransactionRepositoryTest extends TestCase {

    public void testAjouterEtTrouverTransaction()
	    throws Exception {

	long clientId = System.currentTimeMillis();
	String email = "transaction.test." + clientId + "@gmail.com";
	String numeroCompte = "TC" + clientId;

	Client client = new Client(
		clientId,
		"Test",
		"Transaction",
		"97000000",
		email,
		"123456"
	);

	new ClientRepository().ajouter(client);

	CompteCourant compte = new CompteCourant(
		numeroCompte,
		client,
		500.0
	);
	new CompteRepository().ajouter(compte);

	Transaction transaction = new Transaction(
		0,
		TypeTransaction.DEPOT,
		250.0,
		"Dépôt de test"
	);

	TransactionRepository repository =
		new TransactionRepository();
	repository.ajouter(numeroCompte, transaction);

	List<Transaction> transactions =
		repository.trouverParCompte(numeroCompte);

	assertNotNull(transactions);
	assertEquals(1, transactions.size());
	assertEquals(TypeTransaction.DEPOT,
		transactions.get(0).getType());
	assertEquals(250.0,
		transactions.get(0).getMontant());
	assertEquals("Dépôt de test",
		transactions.get(0).getDescription());
    }
}
