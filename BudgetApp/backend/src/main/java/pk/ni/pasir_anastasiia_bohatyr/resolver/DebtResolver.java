package pk.ni.pasir_anastasiia_bohatyr.resolver;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_anastasiia_bohatyr.model.Debt;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;

@Controller
public class DebtResolver {

    @SchemaMapping(typeName = "Debt", field = "debtor")
    public User resolveDebtor(Debt debt) {
        return debt.getDebtor();
    }

    @SchemaMapping(typeName = "Debt", field = "creditor")
    public User resolveCreditor(Debt debt) {
        return debt.getCreditor();
    }

    @SchemaMapping(typeName = "Debt", field = "group")
    public Group resolveGroup(Debt debt) {
        return debt.getGroup();
    }
}
