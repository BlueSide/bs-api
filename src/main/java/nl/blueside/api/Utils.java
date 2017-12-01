package nl.blueside.api;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;

import nl.blueside.api.Exceptions.MissingHeaderException;

public class Utils
{

    public static Map<String, String> checkRequiredHeaders(String[] requiredHeaders, HttpHeaders requestHeaders) throws MissingHeaderException
    {
        //NOTE: Log all the headers for debugging purposes
        if(Settings.debug)
        {
            SlackMessage sm = new SlackMessage("*Incoming headers:*");
            System.out.println("\n");
            System.out.println("Incoming headers:");
        
            Iterator<String> it = requestHeaders.keySet().iterator();
            while(it.hasNext()){
                String key = (String)it.next();
                String value = requestHeaders.getFirst(key);
                System.out.println(key + ": " + value);
                sm.addLine(key + ": " + value);
            }
            System.out.println("\n");
            sm.send();
        }


        //NOTE: Actually check the required headers
        Map<String, String> headers = new HashMap<String, String>();

        for(String header : requiredHeaders)
        {
            if(requestHeaders.containsKey(header))
            {
                headers.put(header, requestHeaders.get(header).get(0));
            }
            else
            {
                throw new MissingHeaderException(header, "The required header " + header + " is missing.");
            }
        }

        

        return headers;
    }

}
