package com.manara.crmmanarayumnahamphygabriel.rest;

import com.manara.crmmanarayumnahamphygabriel.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserApiController {

    private final UserRepository userRepository;

    public UserApiController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/api/check-email")
    public EmailCheckResponse checkEmail(@RequestParam String email){
        boolean exists = userRepository.findByEmail(email).isPresent();
        return new EmailCheckResponse(exists);
    }

    public static class EmailCheckResponse {
        private boolean exists;
        public EmailCheckResponse(boolean exists){ this.exists = exists; }
        public boolean isExists(){ return exists; }
        public void setExists(boolean exists){ this.exists = exists; }
    }
}