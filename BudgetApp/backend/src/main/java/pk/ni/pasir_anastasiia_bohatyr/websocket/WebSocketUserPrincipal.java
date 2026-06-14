package pk.ni.pasir_anastasiia_bohatyr.websocket;

import org.springframework.stereotype.Component;

import java.security.Principal;

public class WebSocketUserPrincipal implements Principal {

    private final String name;

    public WebSocketUserPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
