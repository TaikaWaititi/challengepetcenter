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
        return "Challenge PetCenter API funcionando! Sprint 4 DevOps - build " + buildId;
    }
}
