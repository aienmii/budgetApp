package pk.ni.pasir_anastasiia_bohatyr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String tags;

    private String notes;

    private LocalDateTime timestamp;

    // -------------------------
    // GETTERY
    // -------------------------

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getTags() {
        return tags;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // -------------------------
    // SETTERY
    // -------------------------

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // -------------------------
    // KONSTRUKTORY
    // -------------------------

    // Konstruktor bezparametrowy
    public Transaction() {
    }

    // Konstruktor z parametrami (bez id)
    public Transaction(Double amount, TransactionType type, String tags, String notes, LocalDateTime timestamp) {
        this.amount = amount;
        this.type = type;
        this.tags = tags;
        this.notes = notes;
        this.timestamp = timestamp;
    }
}