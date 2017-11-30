package nl.blueside.api;

import org.springframework.web.socket.WebSocketSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.socket.TextMessage;
import org.springframework.http.HttpStatus;
import java.io.IOException;

@RestController
public class DashboardTest 
{

    @RequestMapping("/d/{message}")
    private ResponseEntity<String> copy(
        @PathVariable("message") String message,
        HttpServletRequest request) throws IOException
    {
        for(WebSocketSession wss : DashboardSessions.getSessions())
        {
            wss.sendMessage(new TextMessage(message.getBytes()));
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
