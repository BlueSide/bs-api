package nl.blueside.api;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.OnMessage;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import java.io.IOException;
import org.springframework.web.socket.server.standard.SpringConfigurator;

@ServerEndpoint(value = "/d/{test}", configurator = SpringConfigurator.class)
public class EchoEndpoint
{

    private final EchoService echoService;

    @Autowired
    public EchoEndpoint(EchoService echoService) {
        this.echoService = echoService;
    }

    @OnMessage
    public void handleMessage(Session session, String message) {
        System.out.println("message!");
    }
/*
    @OnOpen
    public void onOpen(Session session, @PathParam("test") String username) throws IOException
    {
        System.out.println("onOpen");
        System.out.println(username);
        // Get session and WebSocket connection
    }
 
    @OnMessage
    public void onMessage(Session session, String message) throws IOException
    {
        System.out.println("onMessage");
        // Handle new messages
    }
 
    @OnClose
    public void onClose(Session session) throws IOException
    {
        System.out.println("onClose");
        // WebSocket connection closes
    }
 
    @OnError
    public void onError(Session session, Throwable throwable)
    {
        System.out.println("onError");
        // Do error handling here
    }
*/
}
