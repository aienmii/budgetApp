package pk.ni.pasir_anastasiia_bohatyr.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_anastasiia_bohatyr.dto.GroupTransactionDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.service.CurrentUserService;
import pk.ni.pasir_anastasiia_bohatyr.service.GroupTransactionService;

import java.nio.file.AccessDeniedException;

@Controller
public class GroupTransactionGraphQLController {

    private final GroupTransactionService groupTransactionService;
    private final CurrentUserService currentUserService;

    public GroupTransactionGraphQLController(
            GroupTransactionService groupTransactionService,
            CurrentUserService currentUserService
    ) {
        this.groupTransactionService = groupTransactionService;
        this.currentUserService = currentUserService;
    }

    @MutationMapping
    public Boolean addGroupTransaction(@Argument("groupTransactionDTO") GroupTransactionDTO dto) throws AccessDeniedException {
        User currentUser = currentUserService.getCurrentUser();
        groupTransactionService.addGroupTransaction(dto, currentUser);
        return true;
    }
}