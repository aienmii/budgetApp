package pk.ni.pasir_anastasiia_bohatyr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.User;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByMemberships_User(User user);
}
