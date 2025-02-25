package hackthevalley.posepik_be.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class apiController {
    @GetMapping("/")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/health")
    public String health() {
        return "Healthy";
    }
}
