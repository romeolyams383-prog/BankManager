package com.projet;

import com.projet.model.Client;
import com.projet.repository.ClientRepository;

import junit.framework.TestCase;

public class ClientRepositoryTest extends TestCase {

    public void testAjouterEtRechercherClient() throws Exception {

        Client client = new Client(
                1,
                "Dupont",
                "Jean",
                "97000000",
                "jean.dupont@gmail.com",
                "123456"
        );

        ClientRepository repository =
                new ClientRepository();

        // Ajouter le client dans MySQL
        repository.ajouter(client);

        // Rechercher le client
        Client clientRecupere =
                repository.rechercher(1);

        // Vérifications
        assertNotNull(clientRecupere);
        assertEquals("Dupont", clientRecupere.getNom());
        assertEquals("Jean", clientRecupere.getPrenom());
        assertEquals("97000000", clientRecupere.getTelephone());
        assertEquals(
                "jean.dupont@gmail.com",
                clientRecupere.getEmail()
        );

        System.out.println(
                "Client ajouté et récupéré avec succès !"
        );
    }
}