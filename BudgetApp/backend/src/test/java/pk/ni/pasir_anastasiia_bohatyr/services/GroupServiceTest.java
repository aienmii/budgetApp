package pk.ni.pasir_anastasiia_bohatyr.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import pk.ni.pasir_anastasiia_bohatyr.dto.GroupDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.DebtRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.MembershipRepository;
import pk.ni.pasir_anastasiia_bohatyr.service.CurrentUserService;
import pk.ni.pasir_anastasiia_bohatyr.service.GroupService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock GroupRepository groupRepository;
    @Mock MembershipRepository membershipRepository;
    @Mock DebtRepository debtRepository;
    @Mock
    CurrentUserService currentUserService;

    @InjectMocks
    GroupService groupService;

    // -----------------------------
    // getAllGroups()
    // -----------------------------
    @Test
    void shouldReturnAllGroups() {
        List<Group> groups = List.of(new Group(), new Group());
        when(groupRepository.findAll()).thenReturn(groups);

        List<Group> result = groupService.getAllGroups();

        assertEquals(2, result.size());
        verify(groupRepository).findAll();
    }

    // -----------------------------
    // getMyGroups()
    // -----------------------------
    @Test
    void shouldReturnMyGroups() throws Exception {
        User user = new User();
        user.setId(1L);

        List<Group> groups = List.of(new Group());

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(groupRepository.findByMemberships_User(user)).thenReturn(groups);

        List<Group> result = groupService.getMyGroups();

        assertEquals(1, result.size());
    }

    // -----------------------------
    // createGroup()
    // -----------------------------
    @Test
    void shouldCreateGroupAndAddOwnerAsMember() throws Exception {
        User owner = new User();
        owner.setId(1L);

        GroupDTO dto = new GroupDTO();
        dto.setName("Test Group");

        Group saved = new Group();
        saved.setId(10L);
        saved.setOwner(owner);
        saved.setName("Test Group");

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(groupRepository.save(any(Group.class))).thenReturn(saved);

        Group result = groupService.createGroup(dto);

        assertEquals("Test Group", result.getName());
        verify(membershipRepository).save(any(Membership.class));
    }

    // -----------------------------
    // deleteGroup() success
    // -----------------------------
    @Test
    void shouldDeleteGroupWhenOwner() throws Exception {
        User owner = new User();
        owner.setId(1L);

        Group group = new Group();
        group.setId(10L);
        group.setOwner(owner);

        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(currentUserService.getCurrentUser()).thenReturn(owner);

        groupService.deleteGroup(10L);

        verify(debtRepository).deleteByGroupId(10L);
        verify(membershipRepository).deleteByGroupId(10L);
        verify(groupRepository).delete(group);
    }

    // -----------------------------
    // deleteGroup() forbidden
    // -----------------------------
    @Test
    void shouldRejectDeleteWhenNotOwner() throws Exception {
        User owner = new User();
        owner.setId(1L);

        User other = new User();
        other.setId(2L);

        Group group = new Group();
        group.setId(10L);
        group.setOwner(owner);

        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(currentUserService.getCurrentUser()).thenReturn(other);

        assertThrows(AccessDeniedException.class,
                () -> groupService.deleteGroup(10L));
    }
}
