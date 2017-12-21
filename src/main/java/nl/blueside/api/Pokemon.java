package nl.blueside.api;

import org.springframework.web.bind.annotation.RestController;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.net.URI;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import org.apache.http.StatusLine;
import java.net.URISyntaxException;
import org.json.JSONObject;
import nl.blueside.api.Exceptions.BSException;

public class Pokemon extends DataSource
{

    public Pokemon(SPContext context, String resource, String query)
    {
        super(context, resource, query);
    }

    @Override
    public void run()
    {
        String result = null;
        try
        {
            URI url = new URI(this.query);
            HttpGet httpGet = new HttpGet(url);

            PokemonRequest pkmnRequest = new PokemonRequest();

            JSONObject responseObj = new JSONObject();
            responseObj.put("results", pkmnRequest.executeRequest(httpGet));
            responseObj.put("resource", this.resource);
            responseObj.put("query", this.query);

            DBMessage msg = new DBMessage(responseObj.toString(), DBMessageType.UPDATE);

            for(DashboardSession ds : dashboardSessions)
            {
                ds.send(msg.toString());
            }
        }
        catch(URISyntaxException | IOException | BSException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
