package pk.ni.pasir_anastasiia_bohatyr.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pk.ni.pasir_anastasiia_bohatyr.model.Debt;

import java.time.LocalDateTime;
import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByGroupId(Long groupId);
    void deleteByGroupId(Long groupId);
    List<Debt> findByGroupIdAndCreatedAtBetween(Long groupId, LocalDateTime from, LocalDateTime to);

}
