package com.echo.controller;

import com.echo.domain.EchoMessage;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sg0501095 on 5/14/18.
 */
@RestController
@RequestMapping("/echo-service")
public class EchoController {

    @PostMapping("/echo")
    @ResponseBody
    public EchoMessage echo(@RequestParam(value="message", defaultValue="hello world") String message) {
        return new EchoMessage(message);
    }
}
