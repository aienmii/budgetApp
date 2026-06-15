package pk.ni.pasir_anastasiia_bohatyr.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j; // Add this import
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class GroupNotificationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // email -> sessions
    private final Map<String, Set<WebSocketSession>> sessionsByUser = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        Principal user = (Principal) session.getAttributes().get("user");

        if (user == null) {
            log.warn("WS REJECTED: no user in attributes");
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No user"));
            } catch (Exception ignored) {}
            return;
        }

        String email = user.getName();

        sessionsByUser
                .computeIfAbsent(email, k -> ConcurrentHashMap.newKeySet())
                .add(session);

        log.info("WS CONNECTED: {} session={}", email, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        Principal user = (Principal) session.getAttributes().get("user");

        if (user != null) {
            String email = user.getName();

            Set<WebSocketSession> sessions = sessionsByUser.get(email);

            if (sessions != null) {
                sessions.remove(session);

                if (sessions.isEmpty()) {
                    sessionsByUser.remove(email);
                }
            }

            log.info("WS DISCONNECTED: {}", email);
        }

        log.info("WS CLOSED: {}", status);
    }

    public void sendToUser(String email, Object payload) {

        Set<WebSocketSession> sessions = sessionsByUser.get(email);

        if (sessions == null || sessions.isEmpty()) {
            log.warn("WS NO SESSIONS FOR USER: {}", email);
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(payload);
            TextMessage message = new TextMessage(json);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            }

            log.info("WS SENT TO: {} payload={}", email, json);

        } catch (Exception e) {
            log.error("WS ERROR SERIALIZING MESSAGE", e);
        }
    }
}