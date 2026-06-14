package pk.ni.pasir_anastasiia_bohatyr.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pk.ni.pasir_anastasiia_bohatyr.dto.DebtDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Debt;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.DebtRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.UserRepository;
import pk.ni.pasir_anastasiia_bohatyr.service.CurrentUserService;
import pk.ni.pasir_anastasiia_bohatyr.service.DebtService;
import pk.ni.pasir_anastasiia_bohatyr.service.MembershipService;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebtServiceTest {

    @Mock DebtRepository debtRepository;
    @Mock GroupRepository groupRepository;
    @Mock UserRepository userRepository;
    @Mock MembershipService membershipService;
    @Mock CurrentUserService currentUserService;

    @InjectMocks DebtService debtService;

    // ---------------------------------------------------------
    // getGroupDebts()
    // ---------------------------------------------------------
    @Test
    void shouldReturnGroupDebtsWhenUserIsMember() throws Exception {
        doNothing().when(membershipService).assertCurrentUserIsGroupMember(10L);

        Debt d = new Debt();
        when(debtRepository.findByGroupId(10L)).thenReturn(List.of(d));

        List<Debt> result = debtService.getGroupDebts(10L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldRejectGetGroupDebtsWhenNotMember() throws Exception {
        doThrow(new AccessDeniedException("denied"))
                .when(membershipService).assertCurrentUserIsGroupMember(10L);

        assertThrows(AccessDeniedException.class,
                () -> debtService.getGroupDebts(10L));
    }

    // ---------------------------------------------------------
    // createDebt()
    // ---------------------------------------------------------
    @Test
    void shouldCreateDebt() throws Exception {
        Group group = new Group();
        group.setId(10L);

        User owner = new User();
        owner.setId(999L);
        group.setOwner(owner);

        User debtor = new User();
        debtor.setId(2L);

        User creditor = new User();
        creditor.setId(3L);

        User current = creditor;

        DebtDTO dto = new DebtDTO();
        dto.setGroupId(10L);
        dto.setDebtorId(2L);
        dto.setCreditorId(3L);
        dto.setAmount(50.0);
        dto.setTitle("Test");

        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(userRepository.findById(2L)).thenReturn(Optional.of(debtor));
        when(userRepository.findById(3L)).thenReturn(Optional.of(creditor));

        doNothing().when(membershipService).assertCurrentUserIsGroupMember(10L);
        doNothing().when(membershipService).assertUserIsGroupMember(10L, 2L);
        doNothing().when(membershipService).assertUserIsGroupMember(10L, 3L);

        when(currentUserService.getCurrentUser()).thenReturn(current);

        Debt saved = new Debt();
        saved.setId(100L);

        when(debtRepository.save(any(Debt.class))).thenReturn(saved);

        Debt result = debtService.createDebt(dto);

        assertEquals(100L, result.getId());
    }

    @Test
    void shouldRejectDebtWhenDebtorEqualsCreditor() throws AccessDeniedException {
        DebtDTO dto = new DebtDTO();
        dto.setDebtorId(5L);
        dto.setCreditorId(5L);
        dto.setGroupId(10L);
        dto.setAmount(10.0);
        dto.setTitle("X");

        Group group = new Group();
        group.setId(10L);
        User owner = new User();
        owner.setId(999L);
        group.setOwner(owner);

        User sameUser = new User();
        sameUser.setId(5L);

        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(userRepository.findById(5L)).thenReturn(Optional.of(sameUser)); // debtor
        when(userRepository.findById(5L)).thenReturn(Optional.of(sameUser)); // creditor

        doNothing().when(membershipService).assertCurrentUserIsGroupMember(10L);
        doNothing().when(membershipService).assertUserIsGroupMember(10L, 5L);
        assertThrows(IllegalStateException.class,
                () -> debtService.createDebt(dto));
    }

    @Test
    void shouldRejectCreateDebtWhenUserNotAllowed() throws Exception {
        Group group = new Group();
        group.setId(10L);

        User owner = new User();
        owner.setId(999L);
        group.setOwner(owner); // FIXED

        User debtor = new User();
        debtor.setId(2L);

        User creditor = new User();
        creditor.setId(3L);

        User current = new User();
        current.setId(1000L);

        DebtDTO dto = new DebtDTO();
        dto.setGroupId(10L);
        dto.setDebtorId(2L);
        dto.setCreditorId(3L);
        dto.setAmount(10.0);
        dto.setTitle("X");

        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(userRepository.findById(2L)).thenReturn(Optional.of(debtor));
        when(userRepository.findById(3L)).thenReturn(Optional.of(creditor));

        doNothing().when(membershipService).assertCurrentUserIsGroupMember(10L);
        doNothing().when(membershipService).assertUserIsGroupMember(10L, 2L);
        doNothing().when(membershipService).assertUserIsGroupMember(10L, 3L);

        when(currentUserService.getCurrentUser()).thenReturn(current);

        assertThrows(AccessDeniedException.class,
                () -> debtService.createDebt(dto));
    }

    // ---------------------------------------------------------
    // deleteDebt()
    // ---------------------------------------------------------
    @Test
    void shouldDeleteDebtWhenAllowed() throws Exception {
        Group group = new Group();
        group.setId(10L);

        User owner = new User();
        owner.setId(999L);
        group.setOwner(owner); // FIXED

        User debtor = new User();
        debtor.setId(2L);

        User creditor = new User();
        creditor.setId(3L);

        User current = creditor;

        Debt debt = new Debt();
        debt.setId(5L);
        debt.setGroup(group);
        debt.setDebtor(debtor);
        debt.setCreditor(creditor);

        when(debtRepository.findById(5L)).thenReturn(Optional.of(debt));
        when(currentUserService.getCurrentUser()).thenReturn(current);

        doNothing().when(membershipService).assertCurrentUserIsGroupMember(10L);

        debtService.deleteDebt(5L);

        verify(debtRepository).delete(debt);
    }

    @Test
    void shouldRejectDeleteDebtWhenNotAllowed() throws Exception {
        Group group = new Group();
        group.setId(10L);

        User owner = new User();
        owner.setId(999L);
        group.setOwner(owner);

        User debtor = new User();
        debtor.setId(2L);

        User creditor = new User();
        creditor.setId(3L);

        User current = new User();
        current.setId(1000L);

        Debt debt = new Debt();
        debt.setGroup(group);
        debt.setDebtor(debtor);
        debt.setCreditor(creditor);

        when(debtRepository.findById(5L)).thenReturn(Optional.of(debt));
        when(currentUserService.getCurrentUser()).thenReturn(current);

        doNothing().when(membershipService).assertCurrentUserIsGroupMember(10L);

        assertThrows(AccessDeniedException.class,
                () -> debtService.deleteDebt(5L));
    }
}
