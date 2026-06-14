package pk.ni.pasir_anastasiia_bohatyr.resolver;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_anastasiia_bohatyr.model.Membership;

@Controller
public class MembershipResolver {

    @SchemaMapping(typeName = "Membership", field = "userId")
    public Long resolveUserId(Membership membership) {
        return membership.getUser() != null ? membership.getUser().getId() : null;
    }

    @SchemaMapping(typeName = "Membership", field = "groupId")
    public Long resolveGroupId(Membership membership) {
        return membership.getGroup() != null ? membership.getGroup().getId() : null;
    }

    @SchemaMapping(typeName = "Membership", field = "userEmail")
    public String resolveUserEmail(Membership membership) {
        return membership.getUser() != null ? membership.getUser().getEmail() : null;
    }
}
