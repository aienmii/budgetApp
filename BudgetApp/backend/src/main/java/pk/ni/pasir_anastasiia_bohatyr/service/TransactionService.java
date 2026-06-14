package pk.ni.pasir_anastasiia_bohatyr.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import pk.ni.pasir_anastasiia_bohatyr.dto.BalanceDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.TransactionRepository;
import pk.ni.pasir_anastasiia_bohatyr.model.Transaction;
import pk.ni.pasir_anastasiia_bohatyr.model.TransactionType;
import pk.ni.pasir_anastasiia_bohatyr.dto.TransactionDTO;
import pk.ni.pasir_anastasiia_bohatyr.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repo;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Brak uwierzytelnienia");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono zalogowanego użytkownika: " + email));
    }

    public List<Transaction> getAllTransactions() {
        User user = getCurrentUser();
        return repo.findAllByUser(user);
    }

    public Transaction getTransactionById(Long id) {
        Transaction t = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));

        if (!t.getUser().getEmail().equals(getCurrentUser().getEmail())) {
            throw new AccessDeniedException("Nie masz dostępu do tej transakcji");
        }

        return t;
    }


    public Transaction createTransaction(TransactionDTO dto) {
        Transaction t = new Transaction();
        t.setAmount(dto.getAmount());
        t.setType(TransactionType.valueOf(dto.getType()));
        t.setTags(dto.getTags());
        t.setNotes(dto.getNotes());
        t.setTimestamp(LocalDateTime.now());
        t.setUser(getCurrentUser());
        return repo.save(t);
    }


    public Transaction updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));

        if (!transaction.getUser().getEmail().equals(getCurrentUser().getEmail())) {
            throw new AccessDeniedException("Nie masz dostępu do tej transakcji");
        }

        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(transactionDTO.getType()));
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());

        return repo.save(transaction);
    }

    public void deleteTransaction(Long id) {
        Transaction t = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));

        if (!t.getUser().getEmail().equals(getCurrentUser().getEmail())) {
            throw new AccessDeniedException("Nie masz dostępu do tej transakcji");
        }

        repo.delete(t);
    }

    public BalanceDTO getUserBalance(User user, Float days) {

        List<Transaction> userTransactions;

        if (days != null) {
            LocalDateTime from = LocalDateTime.now().minusDays(days.longValue());
            userTransactions = repo.findAllByUserAndTimestampGreaterThanEqual(user, from);
        } else {
            userTransactions = repo.findByUser(user);
        }

        double income = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return new BalanceDTO(income, expense, income - expense);
    }


}




