package nl.blueside.sp_api;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpEntity;
import org.apache.http.Header;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.ByteArrayEntity;



import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONObject;

public class SPAuthCookies
{
    private final String stsUrl = "https://login.microsoftonline.com/extSTS.srf";

    private SPCredentials credentials;
    
    public String rtFa;
    public String fedAuth;
    public String cookie;
    public String digest;

    //TODO: Put in file
    private static final String uglyAssXMLShouldBeInFile = "<s:Envelope xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:a='http://www.w3.org/2005/08/addressing' xmlns:u='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd'><s:Header><a:Action s:mustUnderstand='1'>http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action><a:ReplyTo><a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address></a:ReplyTo><a:To s:mustUnderstand='1'>https://login.microsoftonline.com/extSTS.srf</a:To><o:Security s:mustUnderstand='1' xmlns:o='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd'><o:UsernameToken><o:Username>{username}</o:Username><o:Password>{password}</o:Password></o:UsernameToken></o:Security></s:Header><s:Body><t:RequestSecurityToken xmlns:t='http://schemas.xmlsoap.org/ws/2005/02/trust'><wsp:AppliesTo xmlns:wsp='http://schemas.xmlsoap.org/ws/2004/09/policy'><a:EndpointReference><a:Address>{address}</a:Address></a:EndpointReference></wsp:AppliesTo><t:KeyType>http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey</t:KeyType><t:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</t:RequestType><t:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</t:TokenType></t:RequestSecurityToken></s:Body></s:Envelope>";

    
    public SPAuthCookies(SPCredentials credentials)
    {
        this.credentials = credentials;

        getAuthenticationCookies();
        refreshDigest();
    }

    public void refreshDigest()
    {
        
        try
        {
            String url = credentials.site + "/_api/contextinfo";
            HttpPost httpPost = new HttpPost(url);
            String payload = "";
            try
            {
                httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
            }
            catch (IOException e)
            {
                System.out.println("Error " + e.getMessage());
                e.printStackTrace();
            }

            
            httpPost.setHeader(HttpHeaders.ACCEPT, "application/json;odata=verbose");
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;odata=verbose");
            httpPost.setHeader("Cookie", rtFa + "; " + fedAuth);

            String result = null;
            CloseableHttpClient httpclient = HttpClients.createDefault();

            try
            {
            try
            {
                CloseableHttpResponse response = httpclient.execute(httpPost);
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
                System.err.println(url);
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
                System.err.println(url);
                System.err.println("IOException: " + ioe.getMessage());
            }
        }
            // Parse JSON Object and store Digest value
            JSONObject jsonObject = new JSONObject(result);
            JSONObject d = (JSONObject)jsonObject.get("d");
            JSONObject GetContextWebInformation = (JSONObject)d.get("GetContextWebInformation");
            this.digest = GetContextWebInformation.get("FormDigestValue").toString();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void getAuthenticationCookies()
    {
        String securityToken = getSecurityToken();
        
        //TODO: Throws a null pointer exception when credentials.url is an invalid site
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try
        {
            //TODO: Sanitize URL?
            String url = credentials.url + "/_forms/default.aspx?wa=wsignin1.0";

            try
            {
                HttpPost httpPost = new HttpPost(url);
                
                httpPost.setEntity(new ByteArrayEntity(securityToken.getBytes("UTF-8")));
                CloseableHttpResponse response = httpclient.execute(httpPost);

                HttpEntity httpEntity = response.getEntity();

                Header[] cookieHeaders = response.getHeaders("Set-Cookie");
                if(cookieHeaders.length >= 2)
                {
                    rtFa = cookieHeaders[0].getValue();
                    fedAuth = cookieHeaders[1].getValue();
                    rtFa = rtFa.substring(0, rtFa.indexOf(';'));
                    fedAuth = fedAuth.substring(0, fedAuth.indexOf(';'));
                }
                else
                {
                    //TODO: Handle when no cookies are returned
                }
                response.close();
            }
            catch(UnknownHostException uhe)
            {
                System.err.println("UnknownHostException: " + uhe.getMessage());
                uhe.printStackTrace();
            }
            catch(IOException ioe)
            {
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
                
    }

        
    private String getSecurityToken()
    {
        String token = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try
        {
            
            try
            {
                //TODO: Sanitize URL?
                HttpPost httpPost = new HttpPost(stsUrl);

                //NOTE: Replace placeholders with actual credentials            
                String saml = uglyAssXMLShouldBeInFile.replace("{username}", credentials.username);
                saml = saml.replace("{password}", credentials.password);
                saml =  saml.replace("{address}", credentials.url);
                httpPost.setEntity(new ByteArrayEntity(saml.getBytes("UTF-8")));
                CloseableHttpResponse response = httpclient.execute(httpPost);

                token = processSecurityTokenResponse(EntityUtils.toString(response.getEntity()));

                response.close();
            }
            catch(UnknownHostException uhe)
            {
                System.err.println("UnknownHostException: " + uhe.getMessage());
                uhe.printStackTrace();
            }
            catch(IOException ioe)
            {
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
        
        return token;

    }

    //TODO: Properly lookup needed data and handle errors
    private String processSecurityTokenResponse(String responseString)
    {
        String securityToken = null;

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(new StringReader(responseString)));

            NodeList faultNodes = doc.getElementsByTagName("S:Fault");
            if(faultNodes.getLength() > 0)
            {
                //TODO: Improve error messaging
                System.err.println("Error getting Security Token:");
                Element fault = (Element)faultNodes.item(0);
                Node faultMessage = fault.getElementsByTagName("psf:text").item(0);
                Node faultText = fault.getElementsByTagName("S:Text").item(0);
                System.err.println(faultText.getTextContent());
                System.err.println(faultMessage.getTextContent());
                System.err.println("--------------------------");
            }
            else
            {
                securityToken = doc.getElementsByTagName("wsse:BinarySecurityToken").item(0).getTextContent();
            }
        }
        catch (SAXParseException err)
        {
            System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
        }
        catch (SAXException e)
        {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        }
        catch (Throwable t)
        {
            t.printStackTrace ();
        }

        return securityToken;
    }

}
