package me.dearluca.liteDB;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebServerController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello world";
    }
}
