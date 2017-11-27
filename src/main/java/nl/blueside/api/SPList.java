package nl.blueside.sp_api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import java.net.URISyntaxException;

public class SPList 
{
    public List<SPListField> fields;
    public List<SPListItem> items;
    
    public SPList(JSONArray jsonItems, JSONArray jsonFields) throws URISyntaxException
    {
        this.fields = new ArrayList<SPListField>();
        this.items = new ArrayList<SPListItem>();

        for(int i = 0; i < jsonFields.length(); ++i)
        {
            JSONObject jsonField = (JSONObject)jsonFields.get(i);
            SPListField spField = new SPListField(jsonField);
            this.fields.add(spField);
        }
        
        for(int i = 0; i < jsonItems.length(); ++i)
        {
            JSONObject jsonItem = (JSONObject)jsonItems.get(i);
            SPListItem spItem = new SPListItem(jsonItem);
            this.items.add(spItem);
        }
        
        //TODO: Make use of convertToPlainJSON already present in SPListItem
        handleMultiChoiceFields(getFieldsByFieldType(SPFieldType.MULTICHOICE));
    }

    public List<SPListField> getFieldsByFieldType(SPFieldType type)
    {
        List<SPListField> result = new ArrayList<SPListField>();
        for(SPListField field : fields)
        {
            if(field.type == type)
            {
                result.add(field);
            }
            
        }
        return result;
    }

    private void handleMultiChoiceFields(List<SPListField> multiChoiceFields)
    {
        for(SPListItem item : items)
        {
            for(SPListField multiChoiceField : multiChoiceFields)
            {
                if(item.getProperty(multiChoiceField.title) != null)
                {
                    if(item.getProperty(multiChoiceField.title).equals(null))
                    {
                        JSONArray emptyJsonArray = new JSONArray();
                        item.setProperty(multiChoiceField.title, emptyJsonArray);
                    }
                    else
                    {
                        JSONObject root = (JSONObject)item.getProperty(multiChoiceField.title);
                        JSONArray results = (JSONArray)root.get("results");
                        item.setProperty(multiChoiceField.title, results);
                    }
                }
            }    
        }
        
    }
}
