package com.projet.util;

import com.projet.exception.ValidationException;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void verifierTexte(
            String valeur,
            String nomChamp)
            throws ValidationException {

        if (valeur == null ||
                valeur.trim().isEmpty()) {

            throw new ValidationException(
                    "Le champ " + nomChamp +
                    " est obligatoire."
            );
        }
    }

    public static void verifierEmail(String email)
            throws ValidationException {

        verifierTexte(email, "email");

        if (!email.contains("@") ||
                !email.contains(".")) {

            throw new ValidationException(
                    "Adresse email invalide."
            );
        }
    }

    public static void verifierMotDePasse(
            String motDePasse)
            throws ValidationException {

        verifierTexte(
                motDePasse,
                "mot de passe"
        );

        if (motDePasse.length() < 4) {

            throw new ValidationException(
                    "Le mot de passe doit contenir " +
                    "au moins 4 caractères."
            );
        }
    }
}