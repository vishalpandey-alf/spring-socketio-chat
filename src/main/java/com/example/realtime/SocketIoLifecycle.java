package com.example.realtime;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SocketIoLifecycle {

    private final SocketIOServer socketServer;

    public SocketIoLifecycle(SocketIOServer socketServer) {
        this.socketServer = socketServer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        socketServer.start();
        System.out.println("Socket.IO ready on configured port");
    }

    @PreDestroy
    public void stop() {
        socketServer.stop();
    }
}
