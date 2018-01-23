package nl.blueside.api;

import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer
{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(dashboardHandler(), "/d").setAllowedOrigins(System.getenv("allowedOrigin"), "http://localhost:4200");
        
    }

    @Bean
    public WebSocketHandler dashboardHandler() {
        return new DashboardHandler();
    }

}
