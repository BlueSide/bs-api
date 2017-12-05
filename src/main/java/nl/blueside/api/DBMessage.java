package nl.blueside.api;

import org.json.JSONObject;

public class DBMessage 
{

    private JSONObject jsonObject;

    //TODO: Add timestamp?
    public DBMessage(String message, DBMessageType type)
    {
        jsonObject = new JSONObject();
        jsonObject.put("type", type.toString());
        jsonObject.put("message", message);
    }

    @Override
    public String toString()
    {
        return this.jsonObject.toString();
    }
}
