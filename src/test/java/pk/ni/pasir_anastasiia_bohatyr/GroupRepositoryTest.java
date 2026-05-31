package pk.ni.pasir_anastasiia_bohatyr;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    public void expectGroupsExist() throws AccessDeniedException {
        //given
        List<Group> all;

        //when
        all = groupRepository.findAll();

        //then
        assert !all.isEmpty();
    }
    
}
