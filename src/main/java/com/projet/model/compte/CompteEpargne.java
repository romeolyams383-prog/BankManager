package com.projet.model.compte;

import com.projet.exception.SoldeInsuffisantException;
import com.projet.exception.ValidationException;
import com.projet.model.Client;

public class CompteEpargne extends Compte {

    private double tauxInteret;

    public CompteEpargne(String numeroCompte,
                         Client client,
                         double tauxInteret)
            throws ValidationException {

        super(numeroCompte, client);

        if (tauxInteret < 0) {
            throw new ValidationException(
                    "Le taux d'intérêt ne peut pas être négatif."
            );
        }

        this.tauxInteret = tauxInteret;
    }

    public double getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(double tauxInteret)
            throws ValidationException {

        if (tauxInteret < 0) {
            throw new ValidationException(
                    "Le taux d'intérêt ne peut pas être négatif."
            );
        }

        this.tauxInteret = tauxInteret;
    }

    @Override
    public void afficherTypeCompte() {
        System.out.println("Type : Compte épargne");
    }
}