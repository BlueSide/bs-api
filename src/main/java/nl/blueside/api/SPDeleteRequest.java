package nl.blueside.sp_api;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.ByteArrayEntity;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;
import nl.blueside.sp_api.Exceptions.SharePointException;

public class SPDeleteRequest extends SPRequest
{
    private HttpDelete httpDelete;

    public SPDeleteRequest(SPContext context, String endpoint, HashMap<String, String> headers) throws IOException, URISyntaxException
    {
        super(context, headers);

        URI url = new URI(endpoint);

        HttpDelete httpDelete = new HttpDelete(url);

        //STUDY: Can we make use of this?
        httpDelete.setHeader("IF-MATCH", "*");
    }

    public JSONObject execute() throws IOException, SharePointException
    {
        return this.executeRequest(this.httpDelete);
    }

}
