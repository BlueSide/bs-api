package nl.blueside.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/sp")
public class SPServices
{

    @GetMapping("cache/flush")
    public ResponseEntity<String> flushCache()
    {
        SPFieldsCache.flush();
        return new ResponseEntity<>(HttpStatus.OK);        
    }

}
