package nl.blueside.sp_api;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONObject;

public class SPTitleCache
{

    private static Map<String, String> data = new HashMap<String, String>();
    
    public static void flush()
    {
        data.clear();
    }

    public static String get(String key)
    {
        if(data.containsKey(key))
        {
            return data.get(key);
        }

        return null;
    }

    public static void add(String key, String value)
    {
        data.put(key, value);
    }
}
