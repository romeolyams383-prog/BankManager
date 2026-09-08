package com.projet.model.compte;

import com.projet.exception.SoldeInsuffisantException;
import com.projet.exception.ValidationException;
import com.projet.model.Client;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Compte {

    protected String numeroCompte;
    protected Client client;
    protected double solde;
    protected LocalDateTime dateCreation;

    protected List<Transaction> historique;

    private static long compteurTransaction = 1;

    public Compte(String numeroCompte, Client client)
            throws ValidationException {

        if (numeroCompte == null ||
                numeroCompte.trim().isEmpty()) {

            throw new ValidationException(
                    "Le numéro de compte est obligatoire."
            );
        }

        if (client == null) {
            throw new ValidationException(
                    "Le client est obligatoire."
            );
        }

        this.numeroCompte = numeroCompte;
        this.client = client;
        this.solde = 0.0;
        this.dateCreation = LocalDateTime.now();
        this.historique = new ArrayList<Transaction>();
    }

    public void deposer(double montant)
            throws ValidationException {

        if (montant <= 0) {
            throw new ValidationException(
                    "Le montant doit être supérieur à zéro."
            );
        }

        solde += montant;

        historique.add(
                new Transaction(
                        compteurTransaction++,
                        TypeTransaction.DEPOT,
                        montant,
                        "Dépôt sur le compte"
                )
        );
    }

    public void retirer(double montant)
            throws SoldeInsuffisantException,
                   ValidationException {

        if (montant <= 0) {
            throw new ValidationException(
                    "Le montant doit être supérieur à zéro."
            );
        }

        if (montant > solde) {
            throw new SoldeInsuffisantException(
                    "Solde insuffisant."
            );
        }

        solde -= montant;

        historique.add(
                new Transaction(
                        compteurTransaction++,
                        TypeTransaction.RETRAIT,
                        montant,
                        "Retrait sur le compte"
                )
        );
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public Client getClient() {
        return client;
    }

    public double getSolde() {
        return solde;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public List<Transaction> getHistorique() {
        return historique;
    }

    public abstract void afficherTypeCompte();
}