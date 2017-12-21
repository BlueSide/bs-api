package nl.blueside.api;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

public class DashboardSession
{

    private static final int VALID_SUBSCRIBTION_TIME = 150; // in days

    private Thread thread;
    
    public String query;
    public String resource;
    
    private WebSocketSession wsSession;
    private Map<String, String> subscriptions;
    
    public DashboardSession(WebSocketSession session)
    {
        this.subscriptions = new HashMap<String, String>();
        this.wsSession = session;
    }

    public void send(String message) throws IOException
    {
        wsSession.sendMessage(new TextMessage(message.getBytes()));
    }

    public String getSessionId()
    {
        return this.wsSession.getId();
    }

}
