package nl.blueside.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.microsoft.aad.adal4j.AuthenticationException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import nl.blueside.sp_api.Exceptions.SharePointException;
import nl.blueside.sp_api.SimpleProfiler.SimpleProfiler;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sp")
public class SPServices
{

    @GetMapping("cache/flush")
    public ResponseEntity<String> flushCache()
    {
        SPFieldsCache.flush();
        return new ResponseEntity<>(HttpStatus.OK);        
    }
/*    
    @GetMapping("aggregate")
    public ResponseEntity<String> aggregate(
        @RequestHeader(Settings.TARGET_SITE) String targetSites,
        @RequestHeader(Settings.TARGET_ROOT) String targetRoot,
        @RequestHeader(Settings.URL_LIST) String urlList,
        @RequestHeader(Settings.FIELDS) String fields,
        @RequestHeader(Settings.TARGET_LIST) String targetList,
        @RequestHeader(Settings.SOURCE_LIST) String sourceList
                                            ) throws UnsupportedEncodingException
    {
        SimpleProfiler functionProfiler = new SimpleProfiler();
        SimpleProfiler profiler = new SimpleProfiler();
        if(Settings.debug)
        {
            functionProfiler.startBlock();
        }
        
        if(Settings.debug) profiler.startBlock();
            
        //NOTE: Hack! URLEncode fields and list titles
        headers.replace(targetList, targetList.replaceAll(" ", "%20"));
        headers.replace(sourceList, sourceList.replaceAll(" ", "%20"));
        headers.replace(Settings.urlList, urlList.replaceAll(" ", "%20"));
        headers.replace(fields, fields.replaceAll(" ", "%20"));

        if(Settings.debug) profiler.endBlock("Check if all headers are present", TimeUnit.MICROSECONDS);
        if(Settings.debug) profiler.startBlock();

        if(Settings.debug)
        {
            for (Map.Entry<String, String> entry : headers.entrySet())
            {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }        

        SPContext ctx = null;
        try
        {
            ctx = SPContext.registerCredentials(targetRoot, targetSite, Settings.applicationId,
                                                authCredentials.username, authCredentials.password);
        }
        catch(AuthenticationException ae)
        {
            return new ResponseEntity<String>(ae.getMessage(), null, HttpStatus.FORBIDDEN);
        }
        catch(Exception e)
        {
            return new ResponseEntity<String>(e.getMessage(), null, HttpStatus.UNAUTHORIZED);
        }

        if(Settings.debug) profiler.endBlock("Authorization");
        if(Settings.debug) profiler.startBlock();

        //NOTE: Get all URLs from the URL list
        JSONArray urls = null;
        try
        {
            String result = ctx.get(
                targetSite + "/_api/web/lists/getByTitle('" + urlList + "')" +
                "/items?$select="+Settings.urlTitle+"," + urlField
                                    );
            if(hasError(result))
            {
                System.err.println("Error while getting items from " + targetSite);
                System.err.println(result);
                return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);                
            }
            else
            {
                urls = spParseResultObject(result);
            }
        }
        catch(URISyntaxException use)
        {
            return new ResponseEntity<String>(use.getMessage(), HttpStatus.BAD_REQUEST);
        }
        

        //NOTE: For every URL, get all items in list and put them in list
        List<SPSubsite> subsites = new ArrayList<SPSubsite>();
        for(int i = 0; i < urls.length(); ++i)
        {
            JSONObject row = (JSONObject)urls.get(i);
            String title = row.getString(Settings.urlTitle);
            String url = row.getString(urlField);

            try
            {
                SPSubsite subsite = new SPSubsite(ctx, title, url, sourceList, fields);
                subsites.add(subsite);
            }
            catch(SharePointException spe)
            {
                return new ResponseEntity<String>(spe.getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch(URISyntaxException use)
            {
                System.err.println(use.getMessage());
                return new ResponseEntity<String>(use.getMessage(), HttpStatus.BAD_REQUEST);
            }

        }
        
        if(Settings.debug) profiler.endBlock("Get subsite URLS from list");
        if(Settings.debug) profiler.startBlock();

        //NOTE: Delete all items in target list
        JSONArray itemsToDelete = null;
        try
        {
            String response = ctx.get(targetSite + "/_api/web/lists/getByTitle('"+targetList+"')/items?$select=Id");
            if(hasError(response))
            {
                System.err.println("Error while getting items from list '" + targetList + "'");
                return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);                
            }
            else
            {
                itemsToDelete = spParseResultObject(response);
            }
        }       
        catch(URISyntaxException use)
        {
            System.err.println(use.getMessage());
            return new ResponseEntity<String>(use.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
        BatchRequest deleteItemRequests = new BatchRequest(targetSite);
        for(Object objItemToDelete : itemsToDelete)
        {
            int itemId = ((JSONObject)objItemToDelete).getInt("Id");

            //STUDY: Can we delete the list itself and create a new one?
            //STUDY: Yes we can, but with batch requests maybe not worth the effort
            deleteItemRequests.addDeleteRequest(
                targetSite + "/_api/web/lists/getByTitle('"+targetList+"')/getItemById(" + itemId + ")"
                                                );
            
        }
        
        try
        {
            BatchResult deleteItemsResult = ctx.batch(deleteItemRequests);
            for(BatchResponse batchResponse : deleteItemsResult.batchResponses)
            {
                if(batchResponse.httpStatus.is4xxClientError() || batchResponse.httpStatus.is5xxServerError())
                {
                    //TODO: Give user more info about why this error has occurred
                    return new ResponseEntity<String>("Error while fetching items from lists in subsites", batchResponse.httpStatus);
                }

                if(!batchResponse.payload.isEmpty())
                {
                    if(hasError(batchResponse.payload))
                    {
                        System.err.println("Error while deleting");
                        System.err.println(batchResponse.payload);
                        return new ResponseEntity<String>(batchResponse.payload, HttpStatus.BAD_REQUEST);
                    }   
                }
            }
        }
        catch(URISyntaxException use)
        {
            return new ResponseEntity<String>(use.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if(Settings.debug) profiler.endBlock("Delete items from target list");
        if(Settings.debug) profiler.startBlock();

        //NOTE: Create all fetched items in target list
        BatchRequest batchRequest = new BatchRequest(headers.get(Settings.targetSite));
        for(SPSubsite subsite : subsites)
        {
            if(subsite != null)
            {
                for(SPListItem listItem : subsite.list.items)
                {

                    batchRequest.addPostRequest(
                        targetSite + "/_api/web/lists/getByTitle('"+targetList+"')/items", listItem.toString()
                                                );       
                }
            }
            else
            {
                return new ResponseEntity<String>("Subsite with URL " + subsite.url + "could not be found", null, HttpStatus.BAD_REQUEST);
            }
            
        }

        try
        {
            BatchResult batchResult = ctx.batch(batchRequest);

            // Check for errors
            for(BatchResponse batchResponse : batchResult.batchResponses)
            {
                if(batchResponse.httpStatus.is4xxClientError() || batchResponse.httpStatus.is5xxServerError())
                {
                    //TODO: Give the user more info about why the error has occurred
                    return new ResponseEntity<String>("There was an error when creating the new List Items", batchResponse.httpStatus);
                }
                
            }
            
        }
        catch(URISyntaxException use)
        {
            System.err.println(use.getMessage());
            return new ResponseEntity<String>(use.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
        if(Settings.debug) profiler.endBlock("Post new items");
        if(Settings.debug) functionProfiler.endBlock("Total time", TimeUnit.SECONDS);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public static boolean hasError(String input)
    {
        JSONObject jsonResponse;
        try
        {
            jsonResponse = new JSONObject(input);            
            return jsonResponse.has("error");
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }

        return true;
    }
    
    public static JSONArray spParseResultObject(String input)
    {
        JSONObject rootObject = new JSONObject(input);
        JSONObject d = (JSONObject)rootObject.get("d");
        return (JSONArray)d.get("results");
    }
*/    
/*
    @PostMapping("/call/{method}")
    public ResponseEntity spPassthrough(
        @PathVariable(value = "method") String pMethod,
        @RequestHeader(required = true) HttpHeaders headers,
        @RequestBody(required = false) String requestBody
                                        ) throws URISyntaxException
    {

        final String username = "";
        final String password = "";
        
        if(!headers.containsKey("X-BS-SPTargetRootSite"))
        {
            System.err.println("Required HTTP Header 'X-BS-SPTargetRootSite' is missing");
            return new ResponseEntity<String>("Required HTTP Header 'X-BS-SPTargetRootSite' is missing", null, HttpStatus.BAD_REQUEST);
        }

        if(!headers.containsKey("X-BS-SPTargetSite"))
        {
            System.err.println("Required HTTP Header 'X-BS-SPTargetSite' is missing");
            return new ResponseEntity<String>("Required HTTP Header 'X-BS-SPTargetSite' is missing", null, HttpStatus.BAD_REQUEST);
        }
        
        if(!headers.containsKey("X-BS-SPCall"))
        {
            System.err.println("Required HTTP Header 'X-BS-SPCall' is missing");
            return new ResponseEntity<String>("Required HTTP Header 'X-BS-SPCall' is missing", null, HttpStatus.BAD_REQUEST);
        }
        
        String rootSiteUrl = headers.get("X-BS-SPRootSite").get(0);
        String fullSiteUrl = headers.get("X-BS-SPSite").get(0);
        String fullApiCall = headers.get("X-BS-SPCall").get(0);
        
        System.out.println("rootSiteUrl: " + rootSiteUrl);
        System.out.println("fullSiteUrl: " + fullSiteUrl);
        System.out.println("fullApiCall: " + fullApiCall);

        SPContext ctx = null;
        try
        {
            ctx = SPContext.registerCredentials(rootSiteUrl, fullSiteUrl, Settings.applicationId, username, password);
        }
        catch(AuthenticationException ae)
        {
            new ResponseEntity<String>(ae.getMessage(), null, HttpStatus.FORBIDDEN);
        }
        catch(Exception e)
        {
            new ResponseEntity<String>(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        

        switch(pMethod.toLowerCase())
        {
            case "get":
                String response = ctx.get(fullApiCall);
                if(hasError(response))
                {
                    System.err.println("Error while getting items from list '" + headers.get(Settings.targetList) + "'");
                    return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);                
                }
                
                return new ResponseEntity<String>(response, HttpStatus.OK);
            case "post":
                if(requestBody == null)
                {
                    String msg = "A Request Body is required when doing a POST Request.";
                    return new ResponseEntity<String>(msg, HttpStatus.BAD_REQUEST);
                }
                String postResult = ctx.post(fullApiCall, requestBody);
                return new ResponseEntity<String>(postResult, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
*/
}
