package pk.ni.pasir_anastasiia_bohatyr.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_anastasiia_bohatyr.dto.GroupDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.service.GroupService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Log4j2
@Controller
public class GroupGraphQLController {

    private final GroupService groupService;

    public GroupGraphQLController(GroupService groupService) {
        this.groupService = groupService;
    }

    @QueryMapping
    public List<Group> groups() throws AccessDeniedException {
        log.info("GroupGraphQLController.getGroups()");
        return groupService.getAllGroups();
    }

    @QueryMapping
    public List<Group> myGroups() throws AccessDeniedException {
        log.info("GroupGraphQLController.getMyGroups()");
        return groupService.getMyGroups();
    }

    @MutationMapping
    public Group createGroup(@Argument("groupDTO") GroupDTO groupDTO) throws AccessDeniedException {
        return groupService.createGroup(groupDTO);
    }

    @MutationMapping
    public Boolean deleteGroup(@Argument Long id) throws AccessDeniedException {
        groupService.deleteGroup(id);
        return true;
    }

}