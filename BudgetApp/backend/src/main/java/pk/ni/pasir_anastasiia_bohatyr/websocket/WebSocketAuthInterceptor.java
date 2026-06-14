package pk.ni.pasir_anastasiia_bohatyr.websocket;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import pk.ni.pasir_anastasiia_bohatyr.model.User;
import pk.ni.pasir_anastasiia_bohatyr.repository.UserRepository;
import pk.ni.pasir_anastasiia_bohatyr.security.JwtUtil;

import java.security.Principal;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            System.out.println("WS ERROR: Not a servlet request");
            return false;
        }

        HttpServletRequest http = servletRequest.getServletRequest();
        String token = http.getParameter("token");

        System.out.println("WS RAW QUERY = " + http.getQueryString());
        System.out.println("WS TOKEN = " + token);

        if (token == null || token.isBlank()) {
            System.out.println("WS ERROR: Missing token");
            return false;
        }

        if (!jwtUtil.validateToken(token)) {
            System.out.println("WS ERROR: Invalid token");
            return false;
        }

        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            System.out.println("WS ERROR: User not found");
            return false;
        }

        attributes.put("user", (Principal) () -> email);

        System.out.println("WS AUTH OK: " + email);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}
