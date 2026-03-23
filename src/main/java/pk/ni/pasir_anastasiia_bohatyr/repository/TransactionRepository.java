package pk.ni.pasir_anastasiia_bohatyr.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.ni.pasir_anastasiia_bohatyr.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}