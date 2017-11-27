package nl.blueside.api;

import java.util.List;
import org.springframework.http.HttpStatus;

public class BatchResponse 
{

    public String httpVersion;
    public HttpStatus httpStatus;
    public String payload;
    
    public BatchResponse(List<String> lines)
    {
        String httpResponseLine = getHttpResponseLine(lines);
        
        this.httpVersion = httpResponseLine.substring(0, httpResponseLine.indexOf(" "));

        int statusCodeStart = httpResponseLine.indexOf(" ") + 1;
        int sc = Integer.parseInt(httpResponseLine.substring(statusCodeStart, statusCodeStart + 3));
        this.httpStatus = HttpStatus.valueOf(sc);

        this.payload = lines.get(lines.size() - 1);
        
    }

    private String getHttpResponseLine(List<String> lines)
    {
        for(String line : lines)
        {
            if(line.contains("HTTP/"))
            {
                // Only one will be present, so we can break the loop now
                return line;
            }
        }

        return null;
    }
}
