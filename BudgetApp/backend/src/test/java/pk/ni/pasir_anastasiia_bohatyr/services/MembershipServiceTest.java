package pk.ni.pasir_anastasiia_bohatyr.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import pk.ni.pasir_anastasiia_bohatyr.dto.MembershipDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.MembershipRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.UserRepository;
import pk.ni.pasir_anastasiia_bohatyr.service.CurrentUserService;
import pk.ni.pasir_anastasiia_bohatyr.service.MembershipService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock MembershipRepository membershipRepository;
    @Mock GroupRepository groupRepository;
    @Mock UserRepository userRepository;
    @Mock
    CurrentUserService currentUserService;

    @InjectMocks
    MembershipService membershipService;

    // -----------------------------
    // getGroupMembers()
    // -----------------------------
    @Test
    void shouldReturnGroupMembersWhenUserIsMember() throws Exception {
        User current = new User();
        current.setId(1L);

        Membership m = new Membership();
        m.setUser(current);

        when(currentUserService.getCurrentUser()).thenReturn(current);
        when(membershipRepository.findByGroupId(10L)).thenReturn(List.of(m));

        List<Membership> result = membershipService.getGroupMembers(10L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldRejectGetGroupMembersWhenNotMember() throws Exception {
        User current = new User();
        current.setId(1L);

        when(currentUserService.getCurrentUser()).thenReturn(current);
        when(membershipRepository.findByGroupId(10L)).thenReturn(List.of());

        assertThrows(AccessDeniedException.class,
                () -> membershipService.getGroupMembers(10L));
    }

    // -----------------------------
    // addMember()
    // -----------------------------
    @Test
    void shouldAddMember() throws Exception {
        User owner = new User();
        owner.setId(1L);

        Group group = new Group();
        group.setId(10L);
        group.setOwner(owner);

        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail("x@mail.com");

        MembershipDTO dto = new MembershipDTO();
        dto.setGroupId(10L);
        dto.setUserEmail("x@mail.com");

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(userRepository.findByEmail("x@mail.com")).thenReturn(Optional.of(newUser));
        when(membershipRepository.findByGroupId(10L)).thenReturn(List.of());

        Membership saved = new Membership();
        saved.setUser(newUser);
        saved.setGroup(group);

        when(membershipRepository.save(any(Membership.class))).thenReturn(saved);
        Membership result = membershipService.addMember(dto);

        assertEquals(newUser, result.getUser());
        assertEquals(group, result.getGroup());
    }

    @Test
    void shouldNotAddDuplicateMember() throws Exception {
        User owner = new User();
        owner.setId(1L);

        Group group = new Group();
        group.setId(10L);
        group.setOwner(owner);

        User existing = new User();
        existing.setId(2L);

        Membership existingMembership = new Membership();
        existingMembership.setUser(existing);

        MembershipDTO dto = new MembershipDTO();
        dto.setGroupId(10L);
        dto.setUserEmail("x@mail.com");

        when(currentUserService.getCurrentUser()).thenReturn(owner);
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(userRepository.findByEmail("x@mail.com")).thenReturn(Optional.of(existing));
        when(membershipRepository.findByGroupId(10L)).thenReturn(List.of(existingMembership));

        assertThrows(IllegalStateException.class,
                () -> membershipService.addMember(dto));
    }

    // -----------------------------
    // removeMember()
    // -----------------------------
    @Test
    void shouldNotRemoveOwner() throws Exception {
        User owner = new User();
        owner.setId(1L);

        Group group = new Group();
        group.setOwner(owner);

        Membership membership = new Membership();
        membership.setId(5L);
        membership.setUser(owner);
        membership.setGroup(group);

        when(membershipRepository.findById(5L)).thenReturn(Optional.of(membership));
        when(currentUserService.getCurrentUser()).thenReturn(owner);

        assertThrows(IllegalStateException.class,
                () -> membershipService.removeMember(5L));
    }

    @Test
    void shouldRemoveMemberWhenOwner() throws Exception {
        User owner = new User();
        owner.setId(1L);

        User member = new User();
        member.setId(2L);

        Group group = new Group();
        group.setOwner(owner);

        Membership membership = new Membership();
        membership.setId(5L);
        membership.setUser(member);
        membership.setGroup(group);

        when(membershipRepository.findById(5L)).thenReturn(Optional.of(membership));
        when(currentUserService.getCurrentUser()).thenReturn(owner);

        membershipService.removeMember(5L);

        verify(membershipRepository).delete(membership);
    }
}
