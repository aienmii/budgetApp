package pk.ni.pasir_anastasiia_bohatyr.resolver;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;

@Controller
public class GroupResolver {

    @SchemaMapping(typeName = "Group", field = "ownerId")
    public Long resolveOwnerId(Group group) {
        return group.getOwner() != null ? group.getOwner().getId() : null;
    }
}
