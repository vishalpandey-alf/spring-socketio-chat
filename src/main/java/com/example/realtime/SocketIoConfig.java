package com.example.realtime;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class SocketIoConfig {

    @Bean
    public SocketIOServer socketIOServer(
            @Value("${socket.host}") String host,
            @Value("${socket.port}") int port,
            @Value("${socket.allowed-origin}") String allowedOrigin) {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setOrigin(allowedOrigin);
        return new SocketIOServer(config);
    }
}
