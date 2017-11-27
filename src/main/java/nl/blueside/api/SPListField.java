package nl.blueside.api;

import org.json.JSONObject;

public class SPListField 
{
    public JSONObject field;

    public SPFieldType type;
    public String title;
    
    public SPListField(JSONObject field)
    {
        this.field = field;

        this.title = field.getString("Title");
        this.type = SPFieldType.values()[field.getInt("FieldTypeKind")];
    }

}
