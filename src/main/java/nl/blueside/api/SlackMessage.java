package nl.blueside.api;

import org.json.JSONObject;
import java.net.URI;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.http.HttpHeaders;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SlackMessage 
{

    private static final Boolean ENABLED = true;

    
    List<String> messages;

    private static final String SLACK_ENDPOINT =
        "https://hooks.slack.com/services/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";
    
    public SlackMessage(String message)
    {
        this.messages = new ArrayList<String>();
        this.addLine(message);
    }

    public void addLine(String line)
    {
        messages.add(line);
    }
    
    public void send()
    {
        if(Settings.slackMessagesEnabled)
        {
            JSONObject payload = new JSONObject();
            payload.put("text", String.join("\n", messages));

            try
            {
            
                URI url = new URI(SLACK_ENDPOINT);
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new ByteArrayEntity(payload.toString().getBytes("UTF-8")));


                // Add mandatory headers
                httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

                String result = null;
                CloseableHttpClient httpclient = HttpClients.createDefault();

                try
                {
                    CloseableHttpResponse response = httpclient.execute(httpPost);
                    HttpEntity httpEntity = response.getEntity();

                    if(httpEntity != null)
                    {
                        result = EntityUtils.toString(response.getEntity());
                    }
                
                    response.close();
                }
                finally
                {
                    try
                    {
                        httpclient.close();
                    }
                    catch(IOException ioe)
                    {
                        System.err.println("IOException: " + ioe.getMessage());
                    }
                }
            }
            catch(URISyntaxException use)
            {
                System.err.println("URISyntaxException: " + use.getMessage());
                use.printStackTrace();
            }
            catch(Exception e)
            {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
