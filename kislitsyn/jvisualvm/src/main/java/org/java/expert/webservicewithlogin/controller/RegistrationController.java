package org.java.expert.webservicewithlogin.controller;

import jakarta.validation.Valid;
import org.java.expert.webservicewithlogin.dto.RegistrationRequest;
import org.java.expert.webservicewithlogin.dto.RegistrationResponse;
import org.java.expert.webservicewithlogin.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse register(@Valid @RequestBody RegistrationRequest request) {
        var user = registrationService.register(request.login(), request.password());
        return new RegistrationResponse(user.getId(), user.getLogin());
    }
}
