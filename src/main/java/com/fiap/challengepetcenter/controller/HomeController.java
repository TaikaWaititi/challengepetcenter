package com.fiap.challengepetcenter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class HomeController {

    @Value("${APP_BUILD_ID:local}")
    private String buildId;

    @GetMapping("/")
    public String home() {
        return "PetCenter API - Entrega 4 demonstrada pelo grupo Code&Paws - build " + buildId;
    }
}
