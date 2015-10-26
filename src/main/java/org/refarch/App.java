package org.refarch;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service")
public class App {

    @RequestMapping("/hello")
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
       
    }

}
