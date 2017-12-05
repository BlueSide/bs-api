package nl.blueside.api;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.Header;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.ByteArrayEntity;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.AuthenticationException;

import javax.naming.ServiceUnavailableException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Date;

public class SPContext
{
    protected AuthenticationResult authentication;

    private SPCredentials credentials;
    private final static String AUTHORITY = "https://login.microsoftonline.com/common/";
    
    private static ArrayList<SPContext> contexts = new ArrayList<SPContext>();
    
    public SPContext(SPCredentials credentials) throws AuthenticationException, Exception
    {
        this.credentials = credentials;
        this.authentication = getAccessTokenFromUserCredentials(credentials);
    }

    public void refreshToken() throws Exception
    {
        //Check for a valid context, renew if necessary
        Date now = new Date();
        if(now.after(this.authentication.getExpiresOnDate()))
        {
            this.authentication = getAccessTokenFromUserCredentials(credentials);
            SlackMessage sm = new SlackMessage("Refreshed JWT for " + credentials.username);
        }
    }
    
    public static SPContext registerCredentials(String url, String site, String applicationId, String username, String password) throws AuthenticationException, Exception
    {
        return registerCredentials(new SPCredentials(url, site, applicationId, username, password));
    }

    public static SPContext registerCredentials(SPCredentials credentials) throws AuthenticationException, Exception
    {
        SPContext context;
        //SPCredentials credentialsToRegister = new SPCredentials(url, username, password);
        Iterator<SPContext> contextIterator = contexts.iterator();
        while(contextIterator.hasNext())
        {
            context = contextIterator.next();
            if(credentials.equals(context.getCredentials()))
            {
                return context;
            }
        }

        //No existing context found. Create a new one and save for future use
        context = new SPContext(credentials);
        contexts.add(context);
        return context;
    }
        
    private static AuthenticationResult getAccessTokenFromUserCredentials(SPCredentials credentials) throws AuthenticationException, Exception
    {
        AuthenticationContext context = null;
        AuthenticationResult result = null;
        ExecutorService service = null;
        try
        {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(AUTHORITY, false, service);
            Future<AuthenticationResult> future = context.acquireToken(
                credentials.url, credentials.applicationId, credentials.username, credentials.password,
                null);
            result = future.get();
        }
        finally
        {
            service.shutdown();
        }

        if (result == null)
        {
            throw new ServiceUnavailableException("authentication result was null");
        }
        return result;
    }

    public BatchResult batch(BatchRequest batchRequest) throws URISyntaxException
    {
        URI url = new URI(batchRequest.site + "/_api/$batch");
        HttpPost httpPost = new HttpPost(url);
        String payload = batchRequest.toString();
        try
        {
            httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
        }
        catch(IOException ioe)
        {
            System.out.println("Error " + ioe.getMessage());
            ioe.printStackTrace();
        }
        
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json;odata=verbose");
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "multipart/mixed; boundary=\"batch_"+batchRequest.getGuid()+"\"");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.authentication.getAccessToken());
        
        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();

        BatchResult batchResult = null;
        try
        {
            try
            {
                CloseableHttpResponse response = httpclient.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                
                result = EntityUtils.toString(response.getEntity());
                batchResult = new BatchResult(result);
                response.close();
            }
            catch(UnknownHostException uhe)
            {
                System.err.println("UnknownHostException: " + uhe.getMessage());
                uhe.printStackTrace();
            }
            catch(IOException ioe)
            {
                System.out.println(ioe.toString());
                System.err.println("IOException: " + ioe.getMessage());                
            }
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
        return batchResult;
    }

    public String get(String restRequestURL) throws URISyntaxException { return get(restRequestURL, null); }
    public String get(String restRequestURL, HashMap<String, String> headers) throws URISyntaxException
    {
        URI url = new URI(restRequestURL);

        HttpGet httpGet = new HttpGet(url);

        if(headers != null)
        {
                
            for (Map.Entry<String, String> entry : headers.entrySet())
            {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
                
        }

        return spRequest(httpGet, this.authentication);
    }

    public String post(String restRequestURL, String payload) throws URISyntaxException
    {
        return post(restRequestURL, payload, null);
    }
    public String post(String restRequestURL, String payload, HashMap<String, String> headers) throws URISyntaxException
    {
        URI url = new URI(restRequestURL);
        HttpPost httpPost = new HttpPost(url);

        try
        {
            httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
        }
        catch(IOException ioe)
        {
            System.out.println("Error " + ioe.getMessage());
            ioe.printStackTrace();
        }
        
        if(headers != null)
        {
                
            for (Map.Entry<String, String> entry : headers.entrySet())
            {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }                
        }
        return spRequest(httpPost, this.authentication);
    }

    public String delete(String restRequestURL) throws URISyntaxException { return delete(restRequestURL, null); }
    public String delete(String restRequestURL, HashMap<String, String> headers) throws URISyntaxException
    {
        URI url = new URI(restRequestURL);

        HttpDelete httpDelete = new HttpDelete(url);

        //STUDY: Can we make use of this?
        httpDelete.setHeader("IF-MATCH", "*");

        if(headers != null)
        {
                
            for (Map.Entry<String, String> entry : headers.entrySet())
            {
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            }
                
        }

        return spRequest(httpDelete, this.authentication);
    }
    
    public static String spRequest(HttpUriRequest request, AuthenticationResult auth)
    {

        request.setHeader(HttpHeaders.ACCEPT, "application/json;odata=verbose");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getAccessToken());

        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try
        {
            try
            {
                CloseableHttpResponse response = httpclient.execute(request);
                HttpEntity httpEntity = response.getEntity();
                
                result = EntityUtils.toString(response.getEntity());
            
                response.close();
            }
            catch(UnknownHostException uhe)
            {
                System.err.println("UnknownHostException: " + uhe.getMessage());
                uhe.printStackTrace();
            }
            catch(IOException ioe)
            {
                System.out.println(ioe.toString());
                System.err.println("IOException: " + ioe.getMessage());                
            }
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

        return result;
    }
    
    public static String doRequest(AuthenticationResult auth, String restRequestURL)
    {
        URI url = null;
        try
        {
            url = new URI(restRequestURL);
        }
        catch(URISyntaxException use)
        {
            use.printStackTrace();
        }

        HttpGet request = new HttpGet(url);
        request.setHeader(HttpHeaders.ACCEPT, "application/json;odata=verbose");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getAccessToken());

        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try
        {
            try
            {
                CloseableHttpResponse response = httpclient.execute(request);
                HttpEntity httpEntity = response.getEntity();
                
                result = EntityUtils.toString(response.getEntity());
            
                response.close();
            }
            catch(UnknownHostException uhe)
            {
                System.err.println("UnknownHostException: " + uhe.getMessage());
                uhe.printStackTrace();
            }
            catch(IOException ioe)
            {
                System.out.println(ioe.toString());
                System.err.println("IOException: " + ioe.getMessage());                
            }
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

        return result;
    }

    public SPCredentials getCredentials()
    {
        return this.credentials;
    }
}
