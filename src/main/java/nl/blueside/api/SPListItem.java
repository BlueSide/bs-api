package nl.blueside.api;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;

public class SPListItem
{
    public String id; // The List ID this item originates from
    public String etag;
    public URI endpoint;
    public JSONObject data;
    public JSONArray fieldData;

    public SPListItem()
    {
        this.data = new JSONObject();
    }
    
    public SPListItem(JSONObject jsonData) throws URISyntaxException
    {
        this.data = jsonData;
        if(data.has("Id"))
        {
            this.id = Integer.toString((Integer)data.get("Id"));
        }

        if(data.has("__metadata"))
        {
            JSONObject metadata = (JSONObject)jsonData.get("__metadata");
            this.etag = metadata.getString("etag");
            this.endpoint = new URI(metadata.getString("uri"));
            data.remove("__metadata");
        }
    }

    public void setFieldData(JSONObject fieldData)
    {
        this.fieldData = (JSONArray)fieldData.get("results");
    }

    public void setProperty(String key, Object value)
    {
        this.data.put(key, value);
    }

    public void removeProperty(String key)
    {
        if(this.data.has(key))
        {
            this.data.remove(key);
        }
    }
    
    //TODO: How do we handle property types?
    public Object getProperty(String value)
    {
        if(this.data.has(value))
        {
            return this.data.get(value);
        }
        
        return null;
    }

    public void convertToPlainJSON()
    {
        List<String> objectsToConvert = new ArrayList<String>();

        // Search for fields that need conversion
        Iterator<?> keys = data.keys();
        while( keys.hasNext() )
        {
            String key = (String)keys.next();

            // Check if the value is a JSON Object
            if(data.get(key) instanceof JSONObject)
            {
                JSONObject jsonObj = (JSONObject)data.get(key);
                
                // Check if it contains a __metadata object
                if(jsonObj.has("__metadata"))
                {
                    objectsToConvert.add(key);
                }
            }

            if(fieldData != null)
            {
                // Get a list of all MultiChoices
                //TODO: Add other types that behave like MultiChoice
                List<String> multiChoiceFields = getFieldsWithType(SPFieldType.MULTICHOICE);
                if(data.get(key).equals(null))
                {
                    //TODO: If Field Type is MultiChoice
                    if(multiChoiceFields.contains(key))
                    {
                        data.put(key, new JSONArray());
                    }            
                }
            }
        }

        // Perform the conversion
        for(String key : objectsToConvert)
        {
            JSONArray results = data.getJSONObject(key).getJSONArray("results");
            data.remove(key);
            data.put(key, results);
        }
    }

    private List<String> getFieldsWithType(SPFieldType fieldType)
    {
        List<String> result = new ArrayList<String>();
        
        for (int i = 0; i < this.fieldData.length(); ++i)
        {
            JSONObject field = this.fieldData.getJSONObject(i);
            
            if(fieldType.getOrdinal() == field.getInt("FieldTypeKind"))
            {
                result.add(field.getString("Title"));
            }
        }

        return (result);
    }
        
    @Override
    public String toString()
    {
        return data.toString();
    }
    
}
