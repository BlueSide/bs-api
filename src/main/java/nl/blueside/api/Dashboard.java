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

    private static final int VALID_SUBSCRIBTION_TIME = 150; // in days

    
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

        System.out.println(payload);
        
        //NOTE: Strip off first character, somehow it's a "?"
        payload = payload.substring(1);
        JSONObject root = new JSONObject(payload);
        JSONArray value = root.getJSONArray("value");

        for (int i = 0; i < value.length(); i++)
        {

            JSONObject change = value.getJSONObject(i);
            System.out.println(change.toString());
            String subscriptionId = change.getString("subscriptionId");
            
            for(DashboardSession ds : DashboardSessions.getSessions())
            {
                String resource = ds.getSubscription(subscriptionId);
                if(resource != null)
                {
                    try
                    {   
                        SPGetRequest gr = new SPGetRequest(ds.getContext(), ds.query);
                        JSONObject responseObj = gr.execute();
                        DBMessage msg = new DBMessage(responseObj.toString(), DBMessageType.UPDATE);
                        ds.send(msg.toString());
                    }
                    catch(URISyntaxException use)
                    {
                        System.err.println(use.getMessage());
                        use.printStackTrace();
                    }
                    
                }
            }            
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public static void subscribe(DashboardSession ds, String resource) throws IOException
    {
        // NOTE: Send the client the initial data firts
        // TODO: Move this to a better place. In the future, we want to return this request as early as possible, because the time of the
        //       first load of a chart (for example) depends on this
        try
        {   
            SPGetRequest gr = new SPGetRequest(ds.getContext(), ds.query);
            JSONObject responseObj = gr.execute();
            responseObj.put("resource", resource);
            DBMessage msg = new DBMessage(responseObj.toString(), DBMessageType.UPDATE);
            ds.send(msg.toString());
        }
        catch(URISyntaxException use)
        {
            System.err.println(use.getMessage());
            use.printStackTrace();
        }
        
        //TODO: Figure out when we need to do a new subscription
        //TODO: Figure out when what we can do on an exisiting subscription
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime then = now.plusDays(VALID_SUBSCRIBTION_TIME);

        JSONObject payload = new JSONObject();
        payload.put("resource", resource);
        payload.put("notificationUrl", Settings.webhookEndpoint);
        payload.put("expirationDateTime", then.toString());
        //payload.put("clientState", ds.getId());

        String subscriptionId = null;
        //TODO: We probably shouldn't create a new Webhook for each Dashboard session
        try
        {   
            SPPostRequest pr = new SPPostRequest(ds.getContext(), resource + "/subscriptions", payload.toString());
            JSONObject responseObj = pr.execute();
            ds.addSubscription(resource, subscriptionId);
        }
        catch(URISyntaxException use)
        {
            System.err.println(use.getMessage());
            use.printStackTrace();
        }


    }
}
