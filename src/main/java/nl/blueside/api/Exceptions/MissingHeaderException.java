package nl.blueside.sp_api.Exceptions;

import nl.blueside.sp_api.SlackMessage;

public class MissingHeaderException extends Exception
{

    private String header;
    
    public MissingHeaderException(String header)
    {
        this. header = header;
    }

    public MissingHeaderException(String header, String message)
    {
        super(message);
        this. header = header;

        SlackMessage sm = new SlackMessage("*Missing header:*");
        sm.addLine(message);
        sm.send();
    }

    public MissingHeaderException (String header, Throwable cause)
    {
        super(cause);
        this. header = header;
    }

    public MissingHeaderException (String header, String message, Throwable cause)
    {
        super(message, cause);
        this.header = header;
    }

    public String getHeader()
    {
        return this.header;
    }
}
