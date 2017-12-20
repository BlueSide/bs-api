package nl.blueside.api;

import java.util.List;
import java.util.ArrayList;
public class DataSources 
{
    private static List<DataSource> dataSources = new ArrayList<DataSource>();

    public static void addDataSource(DataSource dataSource)
    {
        dataSources.add(dataSource);
    }

    public static void removeDataSource(String query)
    {
        dataSources.remove(getDataSourceByQuery(query));
    }

    public static List<DataSource> getDataSources()
    {
        return dataSources;
    }

    public static DataSource getDataSourceByQuery(String query)
    {
        for(DataSource ds : dataSources)
        {
            if(ds.getQuery().equals(query))
            {
                return ds;
            }
        }

        return null;
    }
}
