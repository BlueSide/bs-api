package nl.blueside.api;

import java.util.List;
import java.util.ArrayList;
import org.springframework.web.socket.WebSocketSession;



public class DashboardSessions
{
    private static List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();

    public static void addSession(WebSocketSession wss)
    {
        sessions.add(wss);
    }

    public static List<WebSocketSession> getSessions()
    {
        return sessions;
    }

    public static WebSocketSession getSessionById(String id)
    {
        for(WebSocketSession wss : sessions)
        {
            if(wss.getId().equals(id))
            {
                return wss;
            }
        }

        return null;
    }
}
