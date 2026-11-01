package com.example.realtime;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ChatEvents {

    private final SocketIOServer server;
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    private record Session(String name, String room) { }

    public ChatEvents(SocketIOServer server) {
        this.server = server;
        registerListeners();
    }

    private void registerListeners() {
        server.addConnectListener(client -> {
            client.sendEvent("system_message", "Connected. Join a room.");
        });

        server.addDisconnectListener(client -> {
            Session previous = sessions.remove(client.getSessionId());
            if (previous != null) {
                server.getRoomOperations(previous.room()).sendEvent(
                        "system_message", previous.name() + " disconnected");
            }
        });

        server.addEventListener("join_room", JoinRequest.class,
                (client, request, ack) -> joinRoom(client, request));

        server.addEventListener("send_message", ChatRequest.class,
                (client, request, ack) -> sendMessage(client, request));

        server.addEventListener("typing", Boolean.class,
                (client, typing, ack) -> showTyping(client, typing));
    }

    private void joinRoom(SocketIOClient client, JoinRequest request) {
        if (request == null || request.getRoom() == null ||
                request.getName() == null) {
            client.sendEvent("server_error", "Room and name are required.");
            return;
        }

        String room = request.getRoom().trim();
        String name = request.getName().trim();

        if (!room.matches("[A-Za-z0-9_-]{1,24}") ||
                name.isBlank() || name.length() > 25) {
            client.sendEvent("server_error",
                    "Use a room with 1-24 letters/digits/_/-, and name 1-25 chars.");
            return;
        }

        Session previous = sessions.get(client.getSessionId());
        if (previous != null) {
            client.leaveRoom(previous.room());
        }

        client.joinRoom(room);
        sessions.put(client.getSessionId(), new Session(name, room));
        client.sendEvent("joined_room", room);
        server.getRoomOperations(room).sendEvent(
                "system_message", name + " joined room " + room);
    }

    private void sendMessage(SocketIOClient client, ChatRequest request) {
        Session session = sessions.get(client.getSessionId());
        if (session == null) {
            client.sendEvent("server_error", "Join a room first.");
            return;
        }
        if (request == null || request.getText() == null) {
            client.sendEvent("server_error", "Message is required.");
            return;
        }

        String text = request.getText().trim();
        if (text.isEmpty() || text.length() > 500) {
            client.sendEvent("server_error", "Message must be 1-500 chars.");
            return;
        }

        server.getRoomOperations(session.room()).sendEvent(
                "chat_message", Map.of(
                        "room", session.room(),
                        "name", session.name(),
                        "text", text,
                        "time", Instant.now().toString()));
    }

    private void showTyping(SocketIOClient client, Boolean typing) {
        Session session = sessions.get(client.getSessionId());
        if (session == null || typing == null) {
            return;
        }
        server.getRoomOperations(session.room()).sendEvent(
                "typing", Map.of(
                        "name", session.name(),
                        "typing", typing));
    }
}
