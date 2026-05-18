package pk.ni.pasir_anastasiia_bohatyr.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pk.ni.pasir_anastasiia_bohatyr.dto.GroupTransactionDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Debt;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.DebtRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.MembershipRepository;


import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class GroupTransactionService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final MembershipService membershipService;

    public GroupTransactionService(
            GroupRepository groupRepository,
            MembershipRepository membershipRepository,
            DebtRepository debtRepository,
            MembershipService membershipService
    ) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.debtRepository = debtRepository;
        this.membershipService = membershipService;
    }

    public void addGroupTransaction(GroupTransactionDTO transactionDTO, User currentUser) throws AccessDeniedException {

        Group group = groupRepository.findById(transactionDTO.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono Grupy"));

        membershipService.assertCurrentUserIsGroupMember(group.getId());

        List<Membership> members = membershipRepository.findByGroupId(group.getId());

        if (members.isEmpty()) {
            throw new IllegalStateException("Grupa nie ma członków, nie można dodać transakcji.");
        }

        double amountPerUser = transactionDTO.getAmount() / members.size();
        boolean expense = "EXPENSE".equals(transactionDTO.getType());

        for (Membership member : members) {
            User otherUser = member.getUser();

            if (!otherUser.getId().equals(currentUser.getId())) {

                Debt debt = new Debt();
                debt.setGroup(group);
                debt.setAmount(amountPerUser);
                debt.setTitle(transactionDTO.getTitle());

                if (expense) {
                    debt.setDebtor(otherUser);
                    debt.setCreditor(currentUser);
                } else {
                    debt.setDebtor(currentUser);
                    debt.setCreditor(otherUser);
                }

                debtRepository.save(debt);
            }
        }
    }
}
