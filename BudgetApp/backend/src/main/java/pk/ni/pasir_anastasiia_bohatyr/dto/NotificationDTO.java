package pk.ni.pasir_anastasiia_bohatyr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String type;
    private Long groupId;
    private String groupName;
    private String title;
    private String message;
}