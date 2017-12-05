package nl.blueside.api;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import java.io.IOException;
import javax.servlet.http .HttpSession;
import com.microsoft.aad.adal4j.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import org.json.JSONObject;
import org.json.JSONException;

/* TODO:
 * - Write a HttpSessionHandshakeInterceptor to authenticate before upgrading to a WebSocket connection
 */

public class DashboardHandler extends TextWebSocketHandler
{
    private SPContext context;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws AuthenticationException, Exception, URISyntaxException
    {
        //TODO: Get this info from the client
        this.context = SPContext.registerCredentials("https://bluesidenl.sharepoint.com",
                                                     "https://bluesidenl.sharepoint.com/sites/dev/dashboard",
                                                     Settings.applicationId,
                                                     "admin@bluesidenl.onmicrosoft.com", "Zj5B66YDwrmjj3hw");

        DashboardSession ds = new DashboardSession(session, context);
        DashboardSessions.addSession(ds);

        JSONObject returnObject = new JSONObject();
        returnObject.put("type", "session_created");
        returnObject.put("id", ds.getId());

        ds.send(returnObject.toString());
    }
        
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException
    {
        
        try
        {
            JSONObject payload = new JSONObject(message.getPayload());

            switch(payload.getString("type"))
            {
                case "subscription":
                    DashboardSession ds = DashboardSessions.getSessionById(session.getId());

                    //TODO: Handle this dynamically, one Dashboard probably has multiple queries and resources
                    ds.query = payload.getString("query");
                    Dashboard.subscribe(ds, payload.getString("resource"));
                    break;
            }
        }
        catch(JSONException je)
        {
            JSONObject error = new JSONObject();
            error.put("type", "error");
            error.put("message", je.getMessage());
            session.sendMessage(new TextMessage(error.toString().getBytes()));            
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        System.out.println("Dashboard with id " + session.getId() + " disconnected:");
        System.out.println(status.toString());
        DashboardSessions.removeSession(session.getId());
    }
}
