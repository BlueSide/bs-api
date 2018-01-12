package nl.blueside.api;

import org.json.JSONObject;

public class DashboardData
{

    private JSONObject data;
    
    public DashboardData(JSONObject data, String resource, String query)
    {
        this.data = data;
        this.data.put("resource", resource);
        this.data.put("query", query);

    }

    @Override
    public String toString()
    {
        return this.data.toString();
    }
}
