package pk.ni.pasir_anastasiia_bohatyr.service;

import org.springframework.stereotype.Service;
import pk.ni.pasir_anastasiia_bohatyr.dto.NotificationDTO;
import pk.ni.pasir_anastasiia_bohatyr.model.Group;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.exception.GroupNotificationWebSocketHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class GroupNotificationService {

    private final GroupNotificationWebSocketHandler webSocketHandler;

    public GroupNotificationService(
            GroupNotificationWebSocketHandler webSocketHandler
    ) {
        this.webSocketHandler = webSocketHandler;
    }

    public void sendExpenseNotification(
            Group group,
            User creator,
            User recipient,
            String title
    ) {
        NotificationDTO notification =
                NotificationDTO.builder()
                        .type("GROUP_EXPENSE_ADDED")
                        .groupId(group.getId())
                        .groupName(group.getName())
                        .title(title)
                        .message(
                                creator.getUsername()
                                        + " added expense '"
                                        + title
                                        + "' in group '"
                                        + group.getName()
                                        + "'"
                        )
                        .build();

        webSocketHandler.sendToUser(
                recipient.getEmail(),
                notification
        );
    }
}