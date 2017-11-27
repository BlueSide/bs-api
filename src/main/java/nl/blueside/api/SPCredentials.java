package nl.blueside.api;

public class SPCredentials
{
    public String url;
    public String site;
    public String applicationId;
    public String username;
    public String password;
    
    public SPCredentials(String url, String site, String applicationId, String username, String password)
    {
        this.url = url;
        this.site = site;
        this.applicationId = applicationId;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        if (!SPCredentials.class.isAssignableFrom(obj.getClass()))
        {
            return false;
        }

        final SPCredentials other = (SPCredentials) obj;

        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url))
        {
            return false;
        }

        if ((this.password == null) ? (other.password != null) : !this.password.equals(other.password))
        {
            return false;
        }

        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username))
        {
            return false;
        }

        if ((this.site == null) ? (other.site != null) : !this.site.equals(other.site))
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "url: " + this.url + "\n" +
            "site: " + this.site + "\n" +
            "applicationId: " + this.applicationId + "\n" +
            "username: " + this.username + "\n" +
            "password: " + this.password;
    }
}
