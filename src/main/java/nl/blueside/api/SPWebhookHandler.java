package nl.blueside.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/spwh")
public class SPWebhookHandler
{

    @RequestMapping(method = { RequestMethod.POST })
    private ResponseEntity<String> copy(HttpServletRequest request) throws URISyntaxException
    {
        System.out.println(request.toString());
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
