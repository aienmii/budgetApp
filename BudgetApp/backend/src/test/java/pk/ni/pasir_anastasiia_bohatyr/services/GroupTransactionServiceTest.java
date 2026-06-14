package pk.ni.pasir_anastasiia_bohatyr.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pk.ni.pasir_anastasiia_bohatyr.dto.GroupTransactionDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Debt;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.DebtRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.MembershipRepository;
import pk.ni.pasir_anastasiia_bohatyr.service.GroupTransactionService;
import pk.ni.pasir_anastasiia_bohatyr.service.MembershipService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupTransactionServiceTest {

    @Mock GroupRepository groupRepository;
    @Mock MembershipRepository membershipRepository;
    @Mock DebtRepository debtRepository;
    @Mock
    MembershipService membershipService;

    @InjectMocks
    GroupTransactionService service;

    // -----------------------------
    // addGroupTransaction()
    // -----------------------------
    @Test
    void shouldSplitExpenseBetweenMembers() throws Exception {
        Group group = new Group();
        group.setId(10L);

        User payer = new User();
        payer.setId(1L);

        User u2 = new User();
        u2.setId(2L);

        User u3 = new User();
        u3.setId(3L);

        Membership m1 = new Membership(); m1.setUser(payer);
        Membership m2 = new Membership(); m2.setUser(u2);
        Membership m3 = new Membership(); m3.setUser(u3);

        GroupTransactionDTO dto = new GroupTransactionDTO();
        dto.setGroupId(10L);
        dto.setAmount(90.0);
        dto.setType("EXPENSE");
        dto.setTitle("Dinner");

        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(membershipRepository.findByGroupId(10L))
                .thenReturn(List.of(m1, m2, m3));

        doNothing().when(membershipService).assertCurrentUserIsGroupMember(10L);

        service.addGroupTransaction(dto, payer);

        // 90 / 3 = 30 per user
        verify(debtRepository, times(2)).save(any(Debt.class));
    }

    @Test
    void shouldSplitIncomeBetweenMembers() throws Exception {
        Group group = new Group();
        group.setId(10L);

        User receiver = new User();
        receiver.setId(1L);

        User u2 = new User();
        u2.setId(2L);

        Membership m1 = new Membership(); m1.setUser(receiver);
        Membership m2 = new Membership(); m2.setUser(u2);

        GroupTransactionDTO dto = new GroupTransactionDTO();
        dto.setGroupId(10L);
        dto.setAmount(100.0);
        dto.setType("INCOME");
        dto.setTitle("Bonus");

        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(membershipRepository.findByGroupId(10L))
                .thenReturn(List.of(m1, m2));

        doNothing().when(membershipService).assertCurrentUserIsGroupMember(10L);

        service.addGroupTransaction(dto, receiver);

        verify(debtRepository, times(1)).save(any(Debt.class));
    }

    @Test
    void shouldRejectWhenGroupNotFound() {
        GroupTransactionDTO dto = new GroupTransactionDTO();
        dto.setGroupId(10L);

        when(groupRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.addGroupTransaction(dto, new User()));
    }

    @Test
    void shouldRejectWhenNoMembers() throws Exception {
        Group group = new Group();
        group.setId(10L);

        GroupTransactionDTO dto = new GroupTransactionDTO();
        dto.setGroupId(10L);
        dto.setAmount(50.0);
        dto.setType("EXPENSE");
        dto.setTitle("X");

        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(membershipRepository.findByGroupId(10L)).thenReturn(List.of());

        doNothing().when(membershipService).assertCurrentUserIsGroupMember(10L);

        assertThrows(IllegalStateException.class,
                () -> service.addGroupTransaction(dto, new User()));
    }
}
