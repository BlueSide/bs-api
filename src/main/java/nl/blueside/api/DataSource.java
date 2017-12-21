package nl.blueside.api;

import org.json.JSONObject;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

public class DataSource implements Runnable
{

    protected String resource;
    protected String query;
    private SPContext context;
    protected List<DashboardSession> dashboardSessions;
    
    private static final int VALID_SUBSCRIBTION_TIME = 150; // in days

    public DataSource(SPContext context, String resource, String query)
    {
        this.resource = resource;
        this.query = query;
        this.context = context;
        this.dashboardSessions = new ArrayList<DashboardSession>();
        
        Thread thread = new Thread(this);
        thread.start();
    }

    public void addSession(DashboardSession dashboardSession)
    {
        this.dashboardSessions.add(dashboardSession);
    }

    public String getQuery()
    {
        return this.query;
    }
    
    public void run()
    {
        // NOTE: Send the client the initial data first
        try
        {   
            SPGetRequest gr = new SPGetRequest(this.context, this.query);
            JSONObject responseObj = gr.execute();
            responseObj.put("resource", this.resource);
            responseObj.put("query", this.query);
            DBMessage msg = new DBMessage(responseObj.toString(), DBMessageType.UPDATE);

            for(DashboardSession ds : dashboardSessions)
            {
                ds.send(msg.toString());
            }
        }
        catch(URISyntaxException use)
        {
            System.err.println(use.getMessage());
            use.printStackTrace();
        }
        catch(IOException ie)
        {
            System.err.println(ie.getMessage());
            ie.printStackTrace();
        }        

        //TODO: Figure out when what we can do on an exisiting subscription
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime then = now.plusDays(VALID_SUBSCRIBTION_TIME);

        JSONObject payload = new JSONObject();
        payload.put("resource", this.resource);
        payload.put("notificationUrl", Settings.webhookEndpoint);
        payload.put("expirationDateTime", then.toString());
        //payload.put("clientState", getSessionId());

        String subscriptionId = null;
        try
        {
            SPPostRequest pr = new SPPostRequest(this.context, this.resource + "/subscriptions", payload.toString());
            JSONObject responseObj = pr.execute();
        }
        catch(URISyntaxException use)
        {
            System.err.println(use.getMessage());
            use.printStackTrace();
        }
    }
    
}
