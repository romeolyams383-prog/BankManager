package com.projet;

import com.projet.model.Client;
import com.projet.repository.ClientRepository;

import junit.framework.TestCase;

public class ClientRepositoryTest extends TestCase {

    public void testAjouterEtRechercherClient() throws Exception {

        long clientId = System.currentTimeMillis();
        String email = "jean.dupont." + clientId + "@gmail.com";

        Client client = new Client(
                clientId,
                "Dupont",
                "Jean",
                "97000000",
                email,
                "123456"
        );

        ClientRepository repository =
                new ClientRepository();

        // Ajouter le client dans MySQL
        repository.ajouter(client);

        // Rechercher le client
        Client clientRecupere =
                repository.rechercher(clientId);

        // Vérifications
        assertNotNull(clientRecupere);
        assertEquals("Dupont", clientRecupere.getNom());
        assertEquals("Jean", clientRecupere.getPrenom());
        assertEquals("97000000", clientRecupere.getTelephone());
        assertEquals(
                email,
                clientRecupere.getEmail()
        );

        System.out.println(
                "Client ajouté et récupéré avec succès !"
        );
    }
}