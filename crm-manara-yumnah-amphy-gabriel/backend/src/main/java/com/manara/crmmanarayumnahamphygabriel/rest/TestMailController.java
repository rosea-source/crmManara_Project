package com.manara.crmmanarayumnahamphygabriel.rest;

import com.manara.crmmanarayumnahamphygabriel.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/mail-test")
    public String mailTest() {

        emailService.sendEmail(
                "tonemail@gmail.com",
                "Test SMTP",
                "Ça marche 🚀"
        );

        return "OK EMAIL ENVOYÉ";
    }
}