package nl.blueside.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings
{

    private static final String SETTINGS_FILENAME = "settings.properties";

    public static Boolean debug = true; 
    public static Boolean slackMessagesEnabled = true; 
    
    public static String applicationId;
    public static String tenantId;

    public static String urlTitle;
    public static String uriField;

    
    public static final String SP_CONTEXT = "spContext";

    public static final String TARGET_ROOT = "X-BS-SPTargetSiteRoot";
    public static final String TARGET_SITE = "X-BS-SPTargetSite";
    public static final String TARGET_SITES = "X-BS-SPTargetSites";
    public static final String URL_LIST = "X-BS-SPUrlList";
    public static final String URL_FIELD = "X-BS-SPUrlField";
    public static final String FIELDS = "X-BS-SPFields";
    public static final String TARGET_LIST = "X-BS-SPTargetList";
    public static final String SOURCE_LIST = "X-BS-SPSourceList";
    public static final String SOURCE_SITE = "X-BS-SPSourceSite";
    public static final String SOURCE_URI = "X-BS-SPSourceUri";
    public static final String OVERRIDE_TITLE_FIELD= "X-BS-SPSourceTitle";

    // Radar
    public static String radarListGuid;
    public static String radarSubUrlField;
    public static String radarTargetsField;
    public static String radarListTitleField;
    public static String radarTargetUrisField;
    
    private Properties prop;
    
    public Settings()
    {
        this.prop = getPropertiesFromFile(SETTINGS_FILENAME);

        Settings.debug = Boolean.parseBoolean(prop.getProperty("debugMode"));
        Settings.slackMessagesEnabled = Boolean.parseBoolean(prop.getProperty("slackMessagesEnabled"));
        
        Settings.urlTitle = getSetting("urlTitle");
        Settings.applicationId = getSetting("applicationId");
        Settings.tenantId = getSetting("tenantId");
        Settings.uriField = getSetting("uriField");

        Settings.radarListGuid = getSetting("radarListGuid");
        Settings.radarSubUrlField = getSetting("radarSubUrlField");
        Settings.radarTargetsField = getSetting("radarTargetsField");
        Settings.radarListTitleField = getSetting("radarListTitleField");
        Settings.radarTargetUrisField = getSetting("radarTargetUrisField");
    }

    private String getSetting(String setting)
    {
        String result = System.getenv(setting);

        if(result == null)
        {
            result = prop.getProperty(setting);
        }

        return result;
    }
    
    private Properties getPropertiesFromFile(String filename)
    {
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = null;

        try
        {
            input = loader.getResourceAsStream(filename);

    		if(input == null)
            {
                System.out.println("Unable to find " + filename);
    		    return null;
    		}
            
            // load a properties file
            prop.load(input);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return prop;
    }

}
