package nl.blueside.sp_api;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONObject;

public class SPFieldsCache
{

    private static Map<String, JSONObject> data = new HashMap<String, JSONObject>();
    
    public static void flush()
    {
        data.clear();
    }

    public static JSONObject get(String key)
    {
        if(data.containsKey(key))
        {
            return data.get(key);
        }

        return null;
    }

    public static void add(String key, JSONObject value)
    {
        data.put(key, value);
    }
}
