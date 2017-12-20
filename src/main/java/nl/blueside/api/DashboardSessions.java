package nl.blueside.api;

import java.util.List;
import java.util.ArrayList;
import org.springframework.web.socket.WebSocketSession;

public class DashboardSessions
{
    private static List<DashboardSession> sessions = new ArrayList<DashboardSession>();

    public static void addSession(DashboardSession ds)
    {
        sessions.add(ds);
    }

    public static void removeSession(String sessionId)
    {
        
        sessions.remove(getSessionById(sessionId));
    }

    public static List<DashboardSession> getSessions()
    {
        return sessions;
    }

    public static DashboardSession getSessionById(String id)
    {
        for(DashboardSession ds : sessions)
        {
            if(ds.getSessionId().equals(id))
            {
                return ds;
            }
        }

        return null;
    }
}
