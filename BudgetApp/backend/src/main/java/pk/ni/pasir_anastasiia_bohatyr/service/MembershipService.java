package pk.ni.pasir_anastasiia_bohatyr.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pk.ni.pasir_anastasiia_bohatyr.dto.MembershipDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.GroupRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.MembershipRepository;
import pk.ni.pasir_anastasiia_bohatyr.repository.UserRepository;

import java.util.List;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public MembershipService(
            MembershipRepository membershipRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService
    ) {
        this.membershipRepository = membershipRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    public List<Membership> getGroupMembers(Long groupId) throws java.nio.file.AccessDeniedException {
        assertCurrentUserIsGroupMember(groupId);
        return membershipRepository.findByGroupId(groupId);
    }

    public Membership addMember(MembershipDTO membershipDTO) throws java.nio.file.AccessDeniedException {

        assertCurrentUserIsGroupOwner(membershipDTO.getGroupId());

        User user = userRepository.findByEmail(membershipDTO.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono użytkownika o emailu: " + membershipDTO.getUserEmail()
                ));

        Group group = groupRepository.findById(membershipDTO.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono grupy o ID: " + membershipDTO.getGroupId()
                ));

        boolean alreadyMember = membershipRepository.findByGroupId(group.getId())
                .stream()
                .anyMatch(m -> m.getUser().getId().equals(user.getId()));

        if (alreadyMember) {
            throw new IllegalStateException("Użytkownik jest już członkiem tej grupy.");
        }

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setGroup(group);

        return membershipRepository.save(membership);
    }

    public void removeMember(Long membershipId) throws java.nio.file.AccessDeniedException {

        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new EntityNotFoundException("Członkostwo nie istnieje"));

        User currentUser = currentUserService.getCurrentUser();
        User groupOwner = membership.getGroup().getOwner();

        if (!currentUser.getId().equals(groupOwner.getId())) {
            throw new AccessDeniedException("Tylko właściciel grupy może usuwać członków.");
        }

        if (membership.getUser().getId().equals(groupOwner.getId())) {
            throw new IllegalStateException("Nie można usunąć właściciela z jego grupy.");
        }

        membershipRepository.delete(membership);
    }

    public void assertCurrentUserIsGroupMember(Long groupId) throws java.nio.file.AccessDeniedException {
        User currentUser = currentUserService.getCurrentUser();

        boolean isMember = membershipRepository.findByGroupId(groupId)
                .stream()
                .anyMatch(m -> m.getUser().getId().equals(currentUser.getId()));

        if (!isMember) {
            throw new AccessDeniedException("Użytkownik nie jest członkiem tej grupy.");
        }
    }

    public void assertUserIsGroupMember(Long groupId, Long userId) {
        boolean isMember = membershipRepository.findByGroupId(groupId)
                .stream()
                .anyMatch(m -> m.getUser().getId().equals(userId));

        if (!isMember) {
            throw new AccessDeniedException("Użytkownik nie jest członkiem tej grupy.");
        }
    }

    public void assertCurrentUserIsGroupOwner(Long groupId) throws java.nio.file.AccessDeniedException {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupa nie istnieje"));

        User currentUser = currentUserService.getCurrentUser();

        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Tylko właściciel grupy może wykonać tę operację.");
        }
    }
}
