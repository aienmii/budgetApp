package pk.ni.pasir_anastasiia_bohatyr.model;

import lombok.Data;

@Data
public class GroupExpenseNotification {
    private String type;
    private Long groupId;
    private String groupName;
    private String title;
    private double amount;
    private double userShare;
    private String createdByEmail;
    private String message;
}
