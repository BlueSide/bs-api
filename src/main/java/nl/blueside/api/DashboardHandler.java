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
                                                     "admin@bluesidenl.onmicrosoft.com", "vY6TacnqKc8wK4hx");

        DashboardSession ds = new DashboardSession(session);
        DashboardSessions.addSession(ds);

        JSONObject returnObject = new JSONObject();
        returnObject.put("type", DBMessageType.SESSION_CREATED);
        returnObject.put("id", ds.getSessionId());
        ds.send(returnObject.toString());
    }
        
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException
    {
        try
        {
            JSONObject payload = new JSONObject(message.getPayload());

            //TODO: This block takes a long time, resulting in large intervals in Websocket responses
            switch(payload.getString("type"))
            {
                case "subscription":
                    String query = payload.getString("query");
                    String resource = payload.getString("resource");
                    String type = payload.getString("sourceType");
                    DashboardSession dashboardSession = DashboardSessions.getSessionById(session.getId());

                                        
                    // Check if datasource already exists
                    DataSource dataSource = DataSources.getDataSourceByQuery(query);
                    if(dataSource == null)
                    {
                        dataSource = new DataSource(context, resource, query);
                        DataSources.addDataSource(dataSource);
                    }
                    
                    dataSource.addSession(dashboardSession);
                    
                    //TODO: Testcode, remove!
                    if(type.equals("pkmn"))
                    {
                        Pokemon pkmn = new Pokemon(context, resource, query);
                        pkmn.addSession(dashboardSession);
                    }
                    else
                    {

                        if(dataSource == null)
                        {
                            dataSource = new DataSource(context, resource, query);
                        }
                        dataSource.addSession(dashboardSession);

                    }
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
