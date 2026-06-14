package pk.ni.pasir_anastasiia_bohatyr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByGroupId(Long groupId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    void deleteByGroupId(Long groupId);
}
