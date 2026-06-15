package pk.ni.pasir_anastasiia_bohatyr.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pk.ni.pasir_anastasiia_bohatyr.dto.GroupTransactionDTO;
import pk.ni.pasir_anastasiia_bohatyr.dto.TransactionDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Debt;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.DebtRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.MembershipRepository;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupTransactionService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final MembershipService membershipService;
    private final TransactionService transactionService;
    private final GroupNotificationService notificationService;

    public GroupTransactionService(
            GroupRepository groupRepository,
            MembershipRepository membershipRepository,
            DebtRepository debtRepository,
            MembershipService membershipService,
            TransactionService transactionService,
            GroupNotificationService notificationService
    ) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.debtRepository = debtRepository;
        this.membershipService = membershipService;
        this.transactionService = transactionService;
        this.notificationService = notificationService;
    }

    public void addGroupTransaction(
            GroupTransactionDTO transactionDTO,
            User currentUser
    ) throws AccessDeniedException {

        TransactionDTO t = new TransactionDTO();
        t.setAmount(transactionDTO.getAmount());
        t.setType("EXPENSE");
        t.setTags("GROUP_EXPENSE");
        t.setNotes("Wydatek grupowy: " + transactionDTO.getTitle());

        transactionService.createTransaction(t);

        Group group = groupRepository.findById(transactionDTO.getGroupId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Nie znaleziono grupy"));

        membershipService.assertCurrentUserIsGroupMember(group.getId());

        List<Membership> members =
                membershipRepository.findByGroupId(group.getId());

        List<Membership> selectedMembers =
                selectParticipants(transactionDTO, members, currentUser);

        if (selectedMembers.isEmpty()) {
            throw new IllegalStateException(
                    "Grupa nie ma członków, nie można dodać transakcji."
            );
        }

        double amountPerUser =
                transactionDTO.getAmount() / selectedMembers.size();

        boolean expense =
                "EXPENSE".equals(transactionDTO.getType());

        for (Membership member : selectedMembers) {
            User otherUser = member.getUser();

            if (!otherUser.getId().equals(currentUser.getId())) {
                Debt debt = new Debt();

                debt.setDebtor(expense ? otherUser : currentUser);
                debt.setCreditor(expense ? currentUser : otherUser);
                debt.setGroup(group);
                debt.setAmount(amountPerUser);
                debt.setTitle(transactionDTO.getTitle());

                debtRepository.save(debt);

                notificationService.sendExpenseNotification(
                        group,
                        currentUser,
                        otherUser,
                        transactionDTO.getTitle()
                );
            }
        }
    }

    private List<Membership> selectParticipants(
            GroupTransactionDTO transactionDTO,
            List<Membership> members,
            User currentUser
    ) {

        List<Long> selectedUserIds =
                transactionDTO.getSelectedUserIds();

        if (selectedUserIds == null || selectedUserIds.isEmpty()) {
            return members;
        }

        Set<Long> uniqueSelectedUserIds =
                new HashSet<>(selectedUserIds);

        List<Membership> selectedMembers = members.stream()
                .filter(m ->
                        uniqueSelectedUserIds.contains(
                                m.getUser().getId()
                        )
                )
                .toList();

        if (selectedMembers.size() != uniqueSelectedUserIds.size()) {
            throw new IllegalStateException(
                    "Wszyscy wybrani użytkownicy muszą być członkami grupy."
            );
        }

        boolean currentUserSelected = selectedMembers.stream()
                .anyMatch(m ->
                        m.getUser().getId().equals(currentUser.getId())
                );

        if (!currentUserSelected) {
            throw new IllegalStateException(
                    "Aktualny użytkownik musi być uczestnikiem transakcji."
            );
        }

        if (selectedMembers.size() < 2) {
            throw new IllegalStateException(
                    "Transakcja grupowa wymaga co najmniej dwóch uczestników."
            );
        }

        return selectedMembers;
    }


}
