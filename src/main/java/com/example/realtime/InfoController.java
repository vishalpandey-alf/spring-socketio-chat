package com.example.realtime;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final int socketPort;

    public InfoController(@Value("${socket.port}") int socketPort) {
        this.socketPort = socketPort;
    }

    @GetMapping("/api/info")
    public Map<String, Object> info() {
        return Map.of(
                "application", "Spring Boot Socket.IO Chat",
                "status", "ok",
                "socketPort", socketPort,
                "events", new String[]{
                        "join_room", "send_message", "typing"});
    }
}
