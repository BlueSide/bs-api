package nl.blueside.api;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import java.io.IOException;

/* TODO:
 * - Write a HttpSessionHandshakeInterceptor to authenticate before upgrading to a WebSocket connection
 */

public class DashboardHandler extends TextWebSocketHandler
{

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
    {
        System.out.println("A new dashboard has connected with id: " + session.getId());
        DashboardSessions.addSession(session);
    }
        
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException
    {
        System.out.println(message.getPayload());
        String msg = "Hello from API!";
        session.sendMessage(new TextMessage(msg.getBytes()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        System.out.println("Dashboard with id " + session.getId() + " disconnected:");
        System.out.println(status.toString());
        DashboardSessions.removeSession(session);
    }
}
