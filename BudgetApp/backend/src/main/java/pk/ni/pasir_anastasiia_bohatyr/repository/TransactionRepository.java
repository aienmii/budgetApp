package pk.ni.pasir_anastasiia_bohatyr.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.ni.pasir_anastasiia_bohatyr.model.Transaction;
import pk.ni.pasir_anastasiia_bohatyr.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser(User user);
    List<Transaction> findAllByUserAndTimestampGreaterThanEqual(User user, LocalDateTime timestamp);


    List<Transaction> findByUser(User user);
}
