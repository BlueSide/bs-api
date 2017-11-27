package nl.blueside.sp_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application
{
    public static void main(String[] args)
    {
        new Settings();
//        new Database();
        SpringApplication.run(Application.class, args);
    }
}
