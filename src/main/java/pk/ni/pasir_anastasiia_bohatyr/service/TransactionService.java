package pk.ni.pasir_anastasiia_bohatyr.service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import pk.ni.pasir_anastasiia_bohatyr.repository.TransactionRepository;
import pk.ni.pasir_anastasiia_bohatyr.model.Transaction;
import pk.ni.pasir_anastasiia_bohatyr.model.TransactionType;
import pk.ni.pasir_anastasiia_bohatyr.dto.TransactionDTO;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class TransactionService {

    private final TransactionRepository repo;

    public TransactionService(TransactionRepository repo) {
        this.repo = repo;
    }

    public List<Transaction> getAllTransactions() {
        return repo.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));
    }

    public Transaction createTransaction(TransactionDTO dto) {
        Transaction t = new Transaction();
        t.setAmount(dto.getAmount());
        t.setType(TransactionType.valueOf(dto.getType()));
        t.setTags(dto.getTags());
        t.setNotes(dto.getNotes());
        t.setTimestamp(LocalDateTime.now());
        return repo.save(t);
    }

    public Transaction updateTransaction(Long id, TransactionDTO dto) {
        Transaction t = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));

        t.setAmount(dto.getAmount());
        t.setType(TransactionType.valueOf(dto.getType()));
        t.setTags(dto.getTags());
        t.setNotes(dto.getNotes());

        return repo.save(t);
    }

    public void deleteTransaction(Long id) {
        Transaction t = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));
        repo.delete(t);
    }
}
