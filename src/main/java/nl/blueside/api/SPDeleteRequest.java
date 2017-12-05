package nl.blueside.api;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.ByteArrayEntity;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;
import nl.blueside.api.Exceptions.SharePointException;

public class SPDeleteRequest extends SPRequest
{
    private HttpDelete httpDelete;

    public SPDeleteRequest(SPContext context, String endpoint, HashMap<String, String> headers) throws URISyntaxException
    {
        super(context, headers);

        URI url = new URI(endpoint);

        HttpDelete httpDelete = new HttpDelete(url);
    }

    public JSONObject execute()
    {
        JSONObject resultObject = null;

        try
        {
            resultObject = this.executeRequest(this.httpDelete);
        }
        catch(SharePointException spe)
        {
            if(Settings.debug) System.out.println(spe.getMessage());
            spe.printStackTrace();
            return null;
        }
        catch(IOException ie)
        {
            System.out.println(ie.getMessage());
            ie.printStackTrace();
            return null;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }

        return resultObject;
    }

}
