package com.jeffrey.processimageservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jeffrey
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/")
public class RootController {
    @GetMapping
    public String redirect(){
        return "redirect:/access";
    }
}
