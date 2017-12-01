package nl.blueside.api;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;

import nl.blueside.api.Exceptions.SharePointException;

public class SPGetRequest extends SPRequest
{
    private HttpGet httpGet;

    public SPGetRequest(SPContext context, String endpoint) throws URISyntaxException
    {
        super(context);

        URI url = new URI(endpoint);
        this.httpGet = new HttpGet(url);
    }
    
    public SPGetRequest(SPContext context, String endpoint, HashMap<String, String> headers) throws URISyntaxException
    {
        super(context, headers);

        URI url = new URI(endpoint);
        this.httpGet = new HttpGet(url);
    }

    public JSONObject execute()
    {
        JSONObject resultObject = null;

        try
        {
            resultObject = this.executeRequest(this.httpGet);
        }
        catch(SharePointException spe)
        {
            if(Settings.debug) System.out.println(spe.getMessage());
            spe.printStackTrace();
            return null;
        }
        catch(IOException ie)
        {
            if(Settings.debug) System.out.println(ie.getMessage());
            ie.printStackTrace();
            return null;
        }

        return (JSONObject)resultObject.get("d");
    }

}
