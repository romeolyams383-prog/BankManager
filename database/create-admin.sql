USE bank_manager;

INSERT INTO client
    (id, nom, prenoms, telephone, email, mot_de_passe, role)
VALUES
    (900000000001, 'Administrateur', 'BankManager', '00000000',
     'admin@bankmanager.local', 'admin1234', 'ADMIN')
ON DUPLICATE KEY UPDATE
    role = 'ADMIN',
    mot_de_passe = 'admin1234';

INSERT INTO comptes
    (numero_compte, client_id, sold, type_compte,
     decouvert_autorise, `Taux interet`)
SELECT 'ADMIN-00000001', 900000000001, 0.00, 'COURANT', 0.00, 0.0000
WHERE NOT EXISTS (
    SELECT 1 FROM comptes WHERE numero_compte = 'ADMIN-00000001'
);
