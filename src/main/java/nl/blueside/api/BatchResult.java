package nl.blueside.sp_api;

import java.util.List;
import java.util.ArrayList;

public class BatchResult 
{

    public List<BatchResponse> batchResponses;
    
    public BatchResult(String responseString)
    {
        batchResponses = new ArrayList<BatchResponse>();

        // Split the response string into iteratable lines
        String lines[] = responseString.split("\\r?\\n");

        int lineLength = lines.length;

        //Store all lines which contain a '--batchresponse_'
        List<Integer> batchResponseLines = new ArrayList<Integer>();
        for(int i = 0; i < lineLength; ++i)
        {
            if(lines[i].length() > 16)
            {
                if(lines[i].substring(0,16).equals("--batchresponse_"))
                {
                    batchResponseLines.add(i);
                }
            }
        }

        //NOTE: We iterate until size - 1 because the last batchresponse string is a terminator
        for(int i = 0; i < batchResponseLines.size() - 1; ++i)
        {
            int startIndex = batchResponseLines.get(i) + 1;
            int endIndex = batchResponseLines.get(i + 1) - 1;

            List<String> responseLines = new ArrayList<String>();
            for(int lineIndex = startIndex; lineIndex <= endIndex; ++lineIndex)
            {
                responseLines.add(lines[lineIndex]);
            }

            this.batchResponses.add(new BatchResponse(responseLines));
        }
    }
}
