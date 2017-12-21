package nl.blueside.api;

import org.apache.http.client.methods.HttpUriRequest;

import org.json.JSONObject;

import java.util.Map;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import nl.blueside.api.Exceptions.BSException;

public abstract class HTTPRequest
{
    protected Map<String, String> headers;
    
    public HTTPRequest()
    {
        this.headers = new HashMap<String, String>();
    }
    public HTTPRequest(Map<String, String> headers)
    {
        if(headers != null)
        {
            this.headers = headers;
        }
        else
        {
            this.headers = new HashMap<String, String>();
        }
    }

    protected abstract JSONObject executeRequest(HttpUriRequest request) throws IOException, UnknownHostException, BSException;
}
