package nl.blueside.sp_api.Exceptions;

import nl.blueside.sp_api.Settings;
import nl.blueside.sp_api.SlackMessage;

public class SharePointException extends Exception
{
    public SharePointException() {}

    public SharePointException(String message)
    {
        super(message);

        SlackMessage sm = new SlackMessage("*SharePoint Error:*");
        sm.addLine(message);
        sm.send();         
    }

    public SharePointException (Throwable cause)
    {
        super(cause);
    }

    public SharePointException (String message, Throwable cause)
    {
        super(message, cause);
    }
}
