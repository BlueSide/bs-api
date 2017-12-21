package nl.blueside.api.Exceptions;

import nl.blueside.api.Settings;
import nl.blueside.api.SlackMessage;

public class BSException extends Exception
{
    public BSException() {}

    public BSException(String message)
    {
        super(message);

        SlackMessage sm = new SlackMessage("*Blue Side API error:*");
        sm.addLine(message);
        sm.send();         
    }

    public BSException (Throwable cause)
    {
        super(cause);
    }

    public BSException (String message, Throwable cause)
    {
        super(message, cause);
    }
}
