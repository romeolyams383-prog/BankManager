package com.projet.model.compte;

import com.projet.exception.SoldeInsuffisantException;
import com.projet.exception.ValidationException;
import com.projet.model.Client;

public class CompteCourant extends Compte {

    private double decouvertAutorise;

    public CompteCourant(String numeroCompte,
                         Client client,
                         double decouvertAutorise)
            throws ValidationException {

        super(numeroCompte, client);

        if (decouvertAutorise < 0) {
            throw new ValidationException(
                    "Le découvert autorisé ne peut pas être négatif."
            );
        }

        this.decouvertAutorise = decouvertAutorise;
    }

    public double getDecouvertAutorise() {
        return decouvertAutorise;
    }

    public void setDecouvertAutorise(double decouvertAutorise)
            throws ValidationException {

        if (decouvertAutorise < 0) {
            throw new ValidationException(
                    "Le découvert autorisé ne peut pas être négatif."
            );
        }

        this.decouvertAutorise = decouvertAutorise;
    }

    @Override
    public void retirer(double montant)
            throws SoldeInsuffisantException,
                   ValidationException {

        if (montant <= 0) {
            throw new ValidationException(
                    "Le montant doit être supérieur à zéro."
            );
        }

        if (montant > solde + decouvertAutorise) {
            throw new SoldeInsuffisantException(
                    "Solde insuffisant. Découvert autorisé dépassé."
            );
        }

        solde -= montant;

        historique.add(
                new Transaction(
                        System.currentTimeMillis(),
                        TypeTransaction.RETRAIT,
                        montant,
                        "Retrait sur compte courant"
                )
        );
    }

    @Override
    public void afficherTypeCompte() {
        System.out.println("Type : Compte courant");
    }
}