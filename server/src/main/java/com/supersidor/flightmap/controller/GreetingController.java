package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.model.Greeting;
import com.supersidor.flightmap.model.HelloMessage;
import com.supersidor.flightmap.security.UserPrincipal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

@Controller
public class GreetingController {


    @MessageMapping("/hello")
    //@SendToUser("/topic/greetings")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message,@AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

}
