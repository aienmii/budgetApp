package pk.ni.pasir_anastasiia_bohatyr.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.ni.pasir_anastasiia_bohatyr.dto.GroupDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.DebtRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.MembershipRepository;

import java.util.List;

@Log4j2
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final CurrentUserService currentUserService;

    public GroupService(
            GroupRepository groupRepository,
            MembershipRepository membershipRepository,
            DebtRepository debtRepository,
            CurrentUserService currentUserService
    ) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.debtRepository = debtRepository;
        this.currentUserService = currentUserService;
    }

    public List<Group> getAllGroups() {
        log.info("Getting all groups");
        return groupRepository.findAll();
    }

    public List<Group> getMyGroups() throws java.nio.file.AccessDeniedException {
        log.info("GroupService.getMyGroups()");
        User currentUser = currentUserService.getCurrentUser();
        return groupRepository.findByMemberships_User(currentUser);
    }

    public Group createGroup(GroupDTO groupDTO) throws java.nio.file.AccessDeniedException {
        log.info("GroupService.createGroup()");

        User owner = currentUserService.getCurrentUser();
        Group group = new Group();
        group.setName(groupDTO.getName());
        group.setOwner(owner);

        Group savedGroup = groupRepository.save(group);

        Membership membership = new Membership();
        membership.setUser(owner);
        membership.setGroup(savedGroup);
        membershipRepository.save(membership);

        return savedGroup;
    }

    @Transactional
    public void deleteGroup(Long id) throws java.nio.file.AccessDeniedException {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie można usunąć grupy. Grupa o ID " + id + " nie istnieje."
                ));

        User currentUser = currentUserService.getCurrentUser();

        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Tylko właściciel grupy może ją usunąć.");
        }

        debtRepository.deleteByGroupId(id);
        membershipRepository.deleteByGroupId(id);
        groupRepository.delete(group);

    }
}
