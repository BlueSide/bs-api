package nl.blueside.api;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONObject;
import java.io.IOException;
import java.net.UnknownHostException;
import nl.blueside.api.Exceptions.BSException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
public class PokemonRequest extends HTTPRequest
{

    public PokemonRequest()
    {
        super();
    }

    @Override
    protected JSONObject executeRequest(HttpUriRequest request) throws IOException, UnknownHostException, BSException
    {
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
        return new JSONObject(result);
    }

}
