package nl.blueside.api;

import org.springframework.web.socket.WebSocketSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.TextMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import org.json.JSONObject;
import java.net.URISyntaxException;

import java.util.Date;
import java.time.LocalDateTime;
import org.json.JSONArray;

@RestController
public class Dashboard
{

    @RequestMapping("/d/broadcast")
    private ResponseEntity<String> broadcast(
        @RequestBody JSONObject payload,
        HttpServletRequest request) throws IOException
    {
        for(DashboardSession ds : DashboardSessions.getSessions())
        {
            ds.send(payload.toString());
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping("/d/webhook")
    private ResponseEntity<String> handleWebhook(
        @RequestBody(required = false) String payload,
        @RequestParam(value = "validationtoken", required = false) String validationToken,
        HttpServletRequest request) throws IOException
    {
        // If it is just a SharePoint check, return the validation token immediately
        if(validationToken != null)
        {
            System.out.println(payload);
            return new ResponseEntity<String>(validationToken, HttpStatus.OK);
        }

        //NOTE: Strip off first character, somehow it's a "?"
        payload = payload.substring(1);
        JSONObject root = new JSONObject(payload);
        JSONArray value = root.getJSONArray("value");

        for (int i = 0; i < value.length(); i++)
        {

            JSONObject change = value.getJSONObject(i);
            String subscriptionId = change.getString("subscriptionId");

            //TODO: Reimplement changes
            DataSource ds = DataSources.getDataSourceById(subscriptionId);
            if(ds != null)
            {
                
                try
                {   
                    SPGetRequest gr = new SPGetRequest(ds.getContext(), ds.query);

                    DashboardData dd= new DashboardData(gr.execute(), ds.resource, ds.query);
                    
                    DBMessage msg = new DBMessage(dd.toString(), DBMessageType.UPDATE);
                    ds.broadcast(msg.toString());
                }
                catch(URISyntaxException use)
                {
                    System.err.println(use.getMessage());
                    use.printStackTrace();
                }
            }    
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
