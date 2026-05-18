package pk.ni.pasir_anastasiia_bohatyr.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pk.ni.pasir_anastasiia_bohatyr.model.Debt;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByGroupId(Long groupId);
    void deleteByGroupId(Long groupId);
}
