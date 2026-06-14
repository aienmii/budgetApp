package pk.ni.pasir_anastasiia_bohatyr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class InfoController {

    @GetMapping("/api/info")
    public  Map<String, String> info() {
        Map<String, String> response = new HashMap<>();
        response.put("appName", "Aplikacja Budżetowa");
        response.put("version", "1.0");
        response.put("message", "Witaj w aplikacji budżetowej stworzonej ze Spring Boot!");
        return response;
    }
}


