package nl.blueside.api;

import java.util.Base64;
import java.nio.charset.Charset;

//TODO: Support more than basic authorization
public class BasicAuthorization 
{

    public String method;
    public String username;
    public String password;
    
    public BasicAuthorization(String authString)
    {
        if (authString.startsWith("Basic"))
        {
            // Authorization: Basic base64credentials
            String base64Credentials = authString.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                                            Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":",2);

            username = values[0];
            password = values[1];
        }
    }
}
