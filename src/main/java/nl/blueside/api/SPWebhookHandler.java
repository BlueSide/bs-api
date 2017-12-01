package nl.blueside.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/spwh")
public class SPWebhookHandler
{

    @RequestMapping(params = {"validationtoken"})
    
    private ResponseEntity<String> validate(@RequestParam(value = "validationtoken") String validationToken) throws URISyntaxException
    {
        System.out.println(validationToken);
        return new ResponseEntity<String>(validationToken, HttpStatus.OK);
    }
}
