package com.projet;

import com.projet.model.Client;
import com.projet.model.compte.Compte;
import com.projet.model.compte.CompteCourant;
import com.projet.repository.ClientRepository;
import com.projet.repository.CompteRepository;

import junit.framework.TestCase;

public class CompteRepositoryTest extends TestCase {

    public void testAjouterRechercherEtMettreAJourCompte()
	    throws Exception {

	long clientId = System.currentTimeMillis();
	String email = "compte.test." + clientId + "@gmail.com";
	String numeroCompte = "CC" + clientId;

	Client client = new Client(
		clientId,
		"Test",
		"Compte",
		"97000000",
		email,
		"123456"
	);

	ClientRepository clientRepository =
		new ClientRepository();
	clientRepository.ajouter(client);

	CompteCourant compte = new CompteCourant(
		numeroCompte,
		client,
		500.0
	);
	compte.deposer(1000.0);

	CompteRepository compteRepository =
		new CompteRepository();
	compteRepository.ajouter(compte);

	Compte compteRecupere =
		compteRepository.rechercher(numeroCompte);

	assertNotNull(compteRecupere);
	assertEquals(numeroCompte, compteRecupere.getNumeroCompte());
	assertEquals(clientId, compteRecupere.getClient().getId());
	assertEquals(1000.0, compteRecupere.getSolde());

	compteRepository.mettreAJourSolde(numeroCompte, 1000.0);

	Compte compteMisAJour =
		compteRepository.rechercher(numeroCompte);

	assertEquals(1000.0, compteMisAJour.getSolde());
    }
}
