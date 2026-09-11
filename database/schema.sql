CREATE DATABASE IF NOT EXISTS bank_manager
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bank_manager;

CREATE TABLE IF NOT EXISTS client (
    id BIGINT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenoms VARCHAR(100) NOT NULL,
    telephone VARCHAR(30) NOT NULL,
    email VARCHAR(255) NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CLIENT',
    PRIMARY KEY (id),
    UNIQUE KEY uq_client_email (email)
) ENGINE=InnoDB;

-- Migration for an existing installation:
-- ALTER TABLE client ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CLIENT';
-- UPDATE client SET role = 'ADMIN' WHERE email = 'admin@bankmanager.local';

CREATE TABLE IF NOT EXISTS comptes (
    numero_compte VARCHAR(50) NOT NULL,
    client_id BIGINT NOT NULL,
    sold DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    type_compte VARCHAR(20) NOT NULL,
    decouvert_autorise DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    `Taux interet` DECIMAL(7, 4) NOT NULL DEFAULT 0.0000,
    PRIMARY KEY (numero_compte),
    CONSTRAINT fk_compte_client
        FOREIGN KEY (client_id) REFERENCES client(id),
    CONSTRAINT chk_compte_solde CHECK (sold >= 0),
    CONSTRAINT chk_compte_type CHECK (type_compte IN ('COURANT', 'EPARGNE'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `transaction` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    compte_numero VARCHAR(50) NOT NULL,
    type_transaction VARCHAR(20) NOT NULL,
    montant DECIMAL(15, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    date_transaction TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_transaction_compte
        FOREIGN KEY (compte_numero) REFERENCES comptes(numero_compte),
    CONSTRAINT chk_transaction_montant CHECK (montant > 0),
    CONSTRAINT chk_transaction_type CHECK (
        type_transaction IN ('DEPOT', 'RETRAIT', 'VIREMENT')
    )
) ENGINE=InnoDB;
