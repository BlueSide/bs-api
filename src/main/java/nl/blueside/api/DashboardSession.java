package nl.blueside.api;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class DashboardSession 
{

    public String query;
    
    private WebSocketSession wsSession;
    private SPContext context;
    private Map<String, String> subscriptions;
    
    public DashboardSession(WebSocketSession session, SPContext context)
    {
        this.subscriptions = new HashMap<String, String>();
        this.wsSession = session;
        this.context = context;
    }

    public void send(String message) throws IOException
    {
        wsSession.sendMessage(new TextMessage(message.getBytes()));
    }

    public String getId()
    {
        return this.wsSession.getId();
    }

    public SPContext getContext()
    {
        return this.context;
    }

    public void addSubscription(String resource, String id)
    {
        this.subscriptions.put(id, resource);
    }

    public String getSubscription(String id)
    {
        return this.subscriptions.get(id);
    }
}
