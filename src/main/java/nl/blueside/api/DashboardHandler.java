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

/* TODO:
 * - Write a HttpSessionHandshakeInterceptor to authenticate before upgrading to a WebSocket connection
 */

public class DashboardHandler extends TextWebSocketHandler
{
    private SPContext context;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws AuthenticationException, Exception, URISyntaxException
    {
        System.out.println("A new dashboard has connected with id: " + session.getId());
        DashboardSessions.addSession(session);

        this.context = SPContext.registerCredentials("https://bluesidenl.sharepoint.com",
                                                     "https://bluesidenl.sharepoint.com/sites/dev/dashboard",
                                                     Settings.applicationId,
                                                     "admin@bluesidenl.onmicrosoft.com", "Zj5B66YDwrmjj3hw");

    }
        
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException, URISyntaxException
    {
        System.out.println(message.getPayload());

        JSONObject payload = new JSONObject();
        payload.put("resource", "https://bluesidenl.sharepoint.com/sites/dev/dashboard");
        payload.put("notificationUrl", "https://blueside-sp-api.herokuapp.com/spwh");
        payload.put("expirationDateTime", "2018-04-27T16:17:57+00:00");

        SPPostRequest postRequest = new SPPostRequest(context, message.getPayload(), payload.toString(), null);
        JSONObject responseObj = postRequest.execute();
        System.out.println(responseObj.toString());
        //String msg = "Hello from API!";
        //session.sendMessage(new TextMessage(msg.getBytes()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        System.out.println("Dashboard with id " + session.getId() + " disconnected:");
        System.out.println(status.toString());
        DashboardSessions.removeSession(session);
    }
}
