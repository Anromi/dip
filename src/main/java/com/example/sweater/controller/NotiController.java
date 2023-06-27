package com.example.sweater.controller;

import com.example.sweater.domain.NotificationEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class NotiController {

    public int time = 3600;

    @PostMapping("/send-notification")
    public String sendNotification(@RequestParam(name = "message") String message) throws IOException {
        NotificationEndpoint.sendNotification(message);
        return "redirect:/";
    }
}
