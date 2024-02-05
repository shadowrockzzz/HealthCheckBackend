package com.servicehealthcheck.sericeMonitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

import com.servicehealthcheck.content.Data;

@Controller
public class HomeController {

    @GetMapping("/home")
    @ResponseBody
    public Data getData(){
        Data data = new Data();
        return data;
    }
}
