package pk.ni.pasir_anastasiia_bohatyr.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_anastasiia_bohatyr.dto.DebtDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Debt;
import pk.ni.pasir_anastasiia_bohatyr.service.DebtService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
public class DebtGraphQLController {

    private final DebtService debtService;

    public DebtGraphQLController(DebtService debtService) {
        this.debtService = debtService;
    }

    @QueryMapping
    public List<Debt> groupDebts(@Argument Long groupId) throws AccessDeniedException {
        return debtService.getGroupDebts(groupId);
    }

    @MutationMapping
    public Debt createDebt(@Argument("debtDTO") DebtDTO debtDTO) throws AccessDeniedException {
        return debtService.createDebt(debtDTO);
    }

    @MutationMapping
    public Boolean deleteDebt(@Argument Long debtId) throws AccessDeniedException {
        debtService.deleteDebt(debtId);
        return true;
    }
}