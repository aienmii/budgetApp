package pk.ni.pasir_anastasiia_bohatyr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import pk.ni.pasir_anastasiia_bohatyr.exception.GroupNotificationWebSocketHandler;
import pk.ni.pasir_anastasiia_bohatyr.websocket.WebSocketAuthInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketAuthInterceptor authInterceptor;
    private final GroupNotificationWebSocketHandler notificationHandler;

    public WebSocketConfig(
            WebSocketAuthInterceptor authInterceptor,
            GroupNotificationWebSocketHandler notificationHandler
    ) {
        this.authInterceptor = authInterceptor;
        this.notificationHandler = notificationHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationHandler, "/ws/group-notifications")
                .addInterceptors(authInterceptor)
                .setAllowedOriginPatterns("*");
    }
}