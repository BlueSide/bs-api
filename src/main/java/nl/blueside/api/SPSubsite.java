package nl.blueside.api;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URISyntaxException;

import nl.blueside.sp_api.Exceptions.SharePointException;

public class SPSubsite
{
    public String title;
    public String url;
    public SPList list;
    
    public SPSubsite(SPContext ctx, String title, String url, String listName, String fields) throws URISyntaxException, SharePointException
    {
        /*
        this.title = title;
        this.url = url;
        
        String itemResponse = ctx.get(url + "/_api/web/lists/getByTitle('" + listName + "')"
                                  + "/items?$select=" + fields);

        String fieldResponse = ctx.get(url + "/_api/web/lists/getByTitle('" + listName + "')"
                                  + "/fields");

        //if(Application.DEBUG) System.out.println(itemResponse);
        //if(Application.DEBUG) System.out.println(fieldResponse);
        
        if(!itemResponse.isEmpty() && !fieldResponse.isEmpty())
        {
            if(SPServices.hasError(itemResponse)) throw new SharePointException(itemResponse);
            if(SPServices.hasError(fieldResponse)) throw new SharePointException(fieldResponse);

            JSONArray objItems = SPServices.spParseResultObject(itemResponse);
            JSONArray objFields = SPServices.spParseResultObject(fieldResponse);
            this.list = new SPList(objItems, objFields);
                    
        }
        */
    }
    
}

