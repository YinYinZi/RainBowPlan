package org.rainbow.deploy.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author K
 * @date 2021/1/27  15:47
 */
@RestController
public class DockerController {

    @RequestMapping("/")
    public String hello() {
        return "Hello Docker";
    }
}
