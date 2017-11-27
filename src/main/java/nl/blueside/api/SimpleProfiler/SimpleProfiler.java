package nl.blueside.sp_api.SimpleProfiler;

import java.util.concurrent.TimeUnit;

/*
 * TODO: Optimize the shit out of this class, every nanosecond creates a small error in the testresults
 * TODO: Output the results in different formats (JSON? CSV?)
 * TODO: Document this class, nice small class to test JavaDocs!
 * TODO: Add decimals, they are now simply truncated (or rounded? I don't even know)
 */

public class SimpleProfiler 
{
    private long startTime;

    public SimpleProfiler()
    {
        
    }

    public void startBlock()
    {
        startTime = System.nanoTime();
    }

    public void endBlock(String msg) { endBlock(msg, null); }
    public void endBlock(String msg, TimeUnit unit)
    {
        logCurrentTime(msg, unit);
        startTime = System.nanoTime();
    }

    public long getCurrentTime(TimeUnit unit)
    {
        long nanoseconds = (System.nanoTime() - startTime);

        //NOTE: Default is milliseconds
        long currentTime = nanoseconds / 1000000;

        if(unit != null)
        {
            switch(unit)
            {
                case NANOSECONDS:
                    currentTime = nanoseconds;
                    break;
                
                case MICROSECONDS:
                    currentTime = nanoseconds / 1000;
                    break;

                case MILLISECONDS:
                    currentTime = nanoseconds / 1000000;
                    break;
                    
                case SECONDS:
                    currentTime = nanoseconds / 1000000000;
                    break;
                    
                case MINUTES:
                    currentTime = nanoseconds / 1000000000 / 60;
                    break;
                
            }
        }

        return currentTime;
    }

    
    public void logCurrentTime(String msg, TimeUnit unit)
    {
        String unitString = "ms";
        
        if(unit != null)
        {
            switch(unit)
            {
                case NANOSECONDS:
                    unitString = "ns";
                    break;
                
                case MICROSECONDS:
                    unitString = "\u00B5"+"s";
                    break;

                case MILLISECONDS:
                    unitString = "ms";
                    break;
                    
                case SECONDS:
                    unitString = "sec";
                    break;
                    
                case MINUTES:
                    unitString = "min";
                    break;
            }
        }

        System.out.println(getCurrentTime(unit) + unitString + ": " + msg);
    }
}
