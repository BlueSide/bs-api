package nl.blueside.api;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class BatchRequest
{
    private static final String HTTP_VERSION = "HTTP/1.1";

    public String site;
    private UUID guid;
    public List<String> body;
    
    public BatchRequest(String site)
    {
        this.site = site;
        this.guid = UUID.randomUUID();
        this.body = new ArrayList<String>();
    }

    public void addGetRequest(String endpoint)
    {
        body.add("--batch_" + guid);
        body.add("Content-Type: application/http");
        body.add("Content-Transfer-Encoding: binary");
        body.add("");
        body.add("GET " + endpoint + " "  + HTTP_VERSION);
        body.add("Accept: application/json;odata=verbose");
        body.add("");
        body.add("");
    }

    public void addPostRequest(String endpoint, String payload)
    {
        UUID changeSetUuid = UUID.randomUUID();
        body.add("--batch_" + guid);
        body.add("Content-Type: multipart/mixed; boundary=changeset_"+changeSetUuid);
        body.add("");
        body.add("--changeset_" + changeSetUuid);
        body.add("Content-Type: application/http");
        body.add("Content-Transfer-Encoding: binary");
        body.add("");
        body.add("POST " + endpoint + " "+ HTTP_VERSION);
        body.add("Accept: application/json;odata=verbose");
        body.add("Content-Type: application/json");
        body.add("");
        body.add(payload);
        body.add("");
        body.add("--changeset_" + changeSetUuid + "--");
    }

    //TODO: TEST!
    public void addDeleteRequest(String endpoint)
    {
        UUID changeSetUuid = UUID.randomUUID();
        body.add("--batch_" + guid);
        body.add("Content-Type: multipart/mixed; boundary=changeset_"+changeSetUuid);
        body.add("");
        body.add("--changeset_" + changeSetUuid);
        body.add("Content-Type: application/http");
        body.add("Content-Transfer-Encoding: binary");
        body.add("");
        body.add("DELETE " + endpoint + " "+ HTTP_VERSION);
        body.add("IF-MATCH: *");
        body.add("Accept: application/json;odata=verbose");
        body.add("Content-Type: application/json");
        body.add("");
        body.add("--changeset_" + changeSetUuid + "--");
    }

    public UUID getGuid()
    {
        return this.guid;
    }
    
    @Override
    public String toString()
    {
        body.add("--batch_" + guid + "--");
        return String.join("\r\n", body);
    }
}
