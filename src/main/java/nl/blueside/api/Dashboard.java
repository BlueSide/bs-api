package nl.blueside.api;


import org.json.JSONObject;
import org.json.JSONArray;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.Session;
import javax.websocket.OnOpen;
import javax.websocket.OnMessage;
import javax.websocket.OnClose;
import javax.websocket.EncodeException;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

@Controller
public class Dashboard {

    @MessageMapping("/d")
    public String greeting() throws Exception {
        System.out.println("Incoming!");
        return "Hello from Websocket Server!";
    }


}

