package pk.ni.pasir_anastasiia_bohatyr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceDTO {
    private double totalIncome;
    private double totalExpense;
    private double balance;
}
