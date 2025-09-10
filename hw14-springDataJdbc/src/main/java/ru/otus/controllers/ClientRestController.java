package ru.otus.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.crm.dto.ClientDto;
import ru.otus.crm.service.ClientService;

@RequiredArgsConstructor
@RestController
public class ClientRestController {

    private final ClientService clientService;

    @PostMapping("/api/client")
    public ClientDto saveClient(@RequestBody ClientDto clientDto) {
        return clientService.save(clientDto);
    }
}
