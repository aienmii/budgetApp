package pk.ni.pasir_anastasiia_bohatyr.services;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import pk.ni.pasir_anastasiia_bohatyr.dto.GroupDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.DebtRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.MembershipRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.UserRepository;
import pk.ni.pasir_anastasiia_bohatyr.service.CurrentUserService;
import pk.ni.pasir_anastasiia_bohatyr.service.GroupService;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupServicesTest {


    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private GroupRepository groupRepository;

    @MockitoBean
    private MembershipRepository membershipRepository;


    @Autowired
    private GroupService groupService;


    @Test
    void createGroup() throws AccessDeniedException {
        //given
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setName("family");

        User user = new User();
        user.setUsername("test");
        when(currentUserService.getCurrentUser()).thenReturn(user);

        Group expectedGroup = new Group();
        expectedGroup.setName(groupDTO.getName());
        expectedGroup.setOwner(user);

        when(groupRepository.save(any(Group.class))).thenReturn(expectedGroup);
        when(membershipRepository.save(any(Membership.class))).thenReturn(new Membership());

        Group returnedGroup = null;

        //when
        returnedGroup = groupService.createGroup(groupDTO);


        //then
        assertNotNull(returnedGroup);
        assertEquals(expectedGroup, returnedGroup);
    }

}

