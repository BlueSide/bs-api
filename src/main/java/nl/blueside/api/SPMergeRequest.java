package nl.blueside.api;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import java.util.Map;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;
import nl.blueside.sp_api.Exceptions.SharePointException;

public class SPMergeRequest extends SPRequest
{

    private HttpPost httpPost;

    public SPMergeRequest(SPContext context, String endpoint, String payload) throws URISyntaxException
    {
        super(context);

        URI url = new URI(endpoint);
        this.httpPost = new HttpPost(url);

        // Set MERGE headers
        httpPost.setHeader("X-HTTP-Method", "MERGE");
        httpPost.setHeader("If-Match", "*");

        try
        {
            httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
        }
        catch(IOException ie)
        {
            System.out.println(ie.getMessage());
            ie.printStackTrace();
        }
    }

    public SPMergeRequest(SPContext context, String endpoint, String payload, Map<String, String> headers) throws URISyntaxException
    {
        super(context, headers);

        URI url = new URI(endpoint);
        this.httpPost = new HttpPost(url);

        // Set MERGE headers
        httpPost.setHeader("X-HTTP-Method", "MERGE");
        httpPost.setHeader("If-Match", "*");

        try
        {
            httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
        }
        catch(IOException ie)
        {
            System.out.println(ie.getMessage());
            ie.printStackTrace();
        }
    }

    public JSONObject execute()
    {
        JSONObject resultObject = null;

        try
        {
            resultObject = this.executeRequest(this.httpPost);
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

        return resultObject;
    }

}
