package pk.ni.pasir_anastasiia_bohatyr.controller;


import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_anastasiia_bohatyr.dto.MembershipDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;
import pk.ni.pasir_anastasiia_bohatyr.service.MembershipService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
public class MembershipGraphQLController {

    private final MembershipService membershipService;

    public MembershipGraphQLController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @QueryMapping
    public List<Membership> groupMembers(@Argument Long groupId) throws AccessDeniedException {
        return membershipService.getGroupMembers(groupId);
    }

    @MutationMapping
    public Membership addMember(@Argument("membershipDTO") MembershipDTO membershipDTO) throws AccessDeniedException {
        return membershipService.addMember(membershipDTO);
    }

    @MutationMapping
    public Boolean removeMember(@Argument Long membershipId) throws AccessDeniedException {
        membershipService.removeMember(membershipId);
        return true;
    }
}
