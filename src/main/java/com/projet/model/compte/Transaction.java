package com.projet.model.compte;

import java.time.LocalDateTime;

public class Transaction  {

    private long id;
    private TypeTransaction type;
    private double montant;
    private LocalDateTime date;
    private String description;

    public Transaction(long id,
                       TypeTransaction type,
                       double montant,
                       String description) {

        this.id = id;
        this.type = type;
        this.montant = montant;
        this.description = description;
        this.date = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public TypeTransaction getType() {
        return type;
    }

    public double getMontant() {
        return montant;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", montant=" + montant +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}