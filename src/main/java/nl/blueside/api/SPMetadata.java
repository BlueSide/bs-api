package nl.blueside.sp_api;

public class SPMetadata
{
    private String type;
    
    public SPMetadata(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "\'__metadata\':{\'type\':\'"+type+"\'}";
    }
}
