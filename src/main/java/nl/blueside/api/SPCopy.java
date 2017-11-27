package nl.blueside.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;

import com.microsoft.aad.adal4j.AuthenticationException;

import java.util.Map;
import java.util.HashMap;
import java.net.URISyntaxException;

import java.util.concurrent.TimeUnit;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.json.JSONArray;

import nl.blueside.sp_api.SimpleProfiler.SimpleProfiler;
import nl.blueside.sp_api.Exceptions.MissingHeaderException;

@RestController
@RequestMapping("/sp/copy")
public class SPCopy extends BSEndpoint
{

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT })
    private ResponseEntity<String> copy(
        @RequestHeader(Settings.TARGET_SITE) String targetSite,
        @RequestHeader(Settings.FIELDS) String fields,
        @RequestHeader(Settings.TARGET_LIST) String targetList,
        @RequestHeader(Settings.SOURCE_URI) String sourceUri,
        @RequestHeader(Settings.SOURCE_LIST) String sourceList,
        @RequestHeader(Settings.SOURCE_SITE) String sourceSite,
        @RequestHeader(required = false, value = Settings.URL_FIELD) String urlField,
        @RequestHeader(required = false, value = Settings.OVERRIDE_TITLE_FIELD) String overrideTitle,
        HttpServletRequest request) throws URISyntaxException
    {
        this.spContext = (SPContext)request.getSession().getAttribute(Settings.SP_CONTEXT);

        //NOTE: "URLEncode" fields and list titles
        targetList = targetList.replaceAll(" ", "%20");
        sourceList = sourceList.replaceAll(" ", "%20");

        //NOTE: Get the item from the source list
        String endpoint = sourceUri + "?$select=" + fields;

        // Ask for the source URI if we want to update the item
        if(request.getMethod().equals("PUT"))
        {
            endpoint += "," + Settings.uriField;
        }

        if(Settings.debug)
        {
            System.out.println("\n");
            System.out.println("Source Item endpoint:");
            System.out.println(endpoint);
            System.out.println("Getting sourceItem...");
        }
        SPGetRequest sourceItemRequest = new SPGetRequest(spContext, endpoint);
        SPListItem sourceItem = new SPListItem(sourceItemRequest.execute());
        if(Settings.debug)
        {
            System.out.println("Source Item:");
            System.out.println(sourceItem.toString());
        }

        if(Settings.debug)
        {
            System.out.println("\n");
            System.out.println("Getting sourceItem's fields...");
        }
        
        // Get the fields data
        JSONObject fieldsData = SPFieldsCache.get(sourceList);
        if(fieldsData == null)
        {
            String fieldsEndpoint = sourceSite + "_api/web/lists/getByTitle('" + sourceList + "')/fields?$filter=FieldTypeKind+eq+15";
            if(Settings.debug)
            {
                System.out.println("Source Item's fields endpoint:");
                System.out.println(fieldsEndpoint);
            }
            SPGetRequest fieldsRequest = new SPGetRequest(spContext, fieldsEndpoint);
            fieldsData = fieldsRequest.execute();
            System.out.println("Fields:");
            System.out.println(fieldsData.toString());
            SPFieldsCache.add(sourceList, fieldsData);
        }
        else
        {
            if(Settings.debug) System.out.println("Found sourceItem's fields in cache!");
        }

        //NOTE: Override the title property with the Subsite title
        System.out.println("Getting source site's title...");
        String sourceSiteTitle = SPTitleCache.get(sourceList);
        if(sourceSiteTitle == null)
        {
            SPGetRequest sourceSiteTitleRequest = new SPGetRequest(spContext, sourceSite + "_api/web/title");
            JSONObject sourceSiteTitleObject = sourceSiteTitleRequest.execute();
            sourceSiteTitle = (String)sourceSiteTitleObject.get("Title");
            if(Settings.debug) System.out.println("Title: " + sourceSiteTitle);
            SPTitleCache.add(sourceList, sourceSiteTitle);
        }
        else
        {
            if(Settings.debug) System.out.println("Found source site's title in cache!");
            if(Settings.debug) System.out.println("Title: " + sourceSiteTitle);
        }
            

        // Set field data and title property
        sourceItem.setFieldData(fieldsData);
        sourceItem.setProperty(Settings.urlTitle, sourceSiteTitle);

        //TODO: Can we lose this in the constructor somehow?
        sourceItem.convertToPlainJSON();

        // Set the source site for user reference when updating
        if(urlField != null)
        {
            sourceItem.setProperty(urlField, sourceSite);
        }
        
        //NOTE: Override the title property, but only if the header is given
        if(overrideTitle != null)
        {
            if(Settings.debug) System.out.println("Overriding the item's title with: " + overrideTitle);
            sourceItem.setProperty(Settings.urlTitle, overrideTitle);
        }
        
        if(request.getMethod().equals("PUT"))
        {
            String mergeEndpoint = (String)sourceItem.getProperty(Settings.uriField);

            // MERGE the item to the target
            if(Settings.debug)
            {
                System.out.println("Updating item located at: " + mergeEndpoint);
            }

            sourceItem.removeProperty(Settings.uriField);

            SPMergeRequest mergeRequest = new SPMergeRequest(spContext, mergeEndpoint, sourceItem.toString(), null);
            JSONObject mergeResultObject = mergeRequest.execute();

            return new ResponseEntity<>(HttpStatus.OK);
        }
        else if(request.getMethod().equals("POST"))
        {
            System.out.println(targetSite + "/_api/web/lists/getByTitle('"+targetList+"')/items");
            SPPostRequest postRequest = new SPPostRequest(
                spContext,
                targetSite + "/_api/web/lists/getByTitle('"+targetList+"')/items",
                sourceItem.toString(),
                null);
            JSONObject postResultObject = postRequest.execute();

            SPListItem createdItem = new SPListItem(postResultObject);
            
            return ResponseEntity.created(createdItem.endpoint).build();
        }
        else
        {
            return new ResponseEntity<String>("Invalid method "+ request.getMethod() +". Only POST and PUT methods are allowed", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

}
