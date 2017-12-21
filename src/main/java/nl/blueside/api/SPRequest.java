package nl.blueside.api;

import org.apache.http.HttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.http.StatusLine;

import org.json.JSONObject;
import org.json.JSONException;

import nl.blueside.api.Exceptions.BSException;

//TODO: Handle all errors by checking the Status Code!
public class SPRequest extends HTTPRequest
{
    protected SPContext context;

    public SPRequest(SPContext context)
    {
        super();
        this.context = context;
        init();
    }

    public SPRequest(SPContext context, Map<String, String> headers)
    {
        super(headers);
        this.context = context;
        init();
    }

    private void init()
    {
        // Refresh access token if needed
        this.context.refreshToken();
        
        // Add mandatory headers
        headers.put(HttpHeaders.ACCEPT, "application/json;odata=verbose");
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + context.authentication.getAccessToken());
    }
    
    @Override
    protected JSONObject executeRequest(HttpUriRequest request) throws IOException, UnknownHostException, BSException
    {
        // Add optional headers        
        if(headers != null)
        {
            for (Map.Entry<String, String> entry : headers.entrySet())
            {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }

        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try
        {

            //Execute the request
            CloseableHttpResponse response = httpclient.execute(request);
            StatusLine statusLine = response.getStatusLine();
                
            HttpEntity httpEntity = response.getEntity();

                
            if(httpEntity != null)
            {
                result = EntityUtils.toString(response.getEntity());

                if(statusLine.getStatusCode() > 299)
                {
                    throw new BSException(statusLine + ": " + result);
                }    
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

        JSONObject responseObject = null;
        
        if( (result != null) && (!result.isEmpty()) )
        {
            try
            {
                responseObject = new JSONObject(result);
                if(responseObject.has("error"))
                {
                    JSONObject error = (JSONObject)responseObject.get("error");
                    JSONObject message = (JSONObject)error.get("message");
                    throw new BSException(message.get("value").toString());
                }
            }
            catch(JSONException je)
            {
                //TODO: This is of course not really a SharePoint exception
                throw new BSException(je.getMessage());
            }
        }
        
        return responseObject;
    }
}
