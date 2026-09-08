package com.projet.model;

import com.projet.exception.ValidationException;
import com.projet.util.ValidationUtils;


public class Client {

    private long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String motDePasse;

    public Client(long id,
                  String nom,
                  String prenom,
                  String telephone,
                  String email,
                  String motDePasse)
            throws ValidationException {

        if (id <= 0) {
            throw new ValidationException(
                    "L'identifiant doit être positif."
            );
        }

        ValidationUtils.verifierTexte(nom, "nom");
        ValidationUtils.verifierTexte(prenom, "prénom");
        ValidationUtils.verifierTexte(telephone, "téléphone");
        ValidationUtils.verifierEmail(email);
        ValidationUtils.verifierMotDePasse(motDePasse);

        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
