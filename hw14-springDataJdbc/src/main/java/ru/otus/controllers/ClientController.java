package ru.otus.controllers;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.crm.dto.ClientDto;
import ru.otus.crm.dto.CreateClientDto;
import ru.otus.crm.service.ClientService;

@RequiredArgsConstructor
@Controller
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/clients")
    public String clientsView(Model model) {
        List<ClientDto> clients = clientService.findAll();
        model.addAttribute("clients", clients);
        model.addAttribute("client", new CreateClientDto());
        return "clients";
    }

    @PostMapping("/client/create")
    public RedirectView clientSave(CreateClientDto createClientDto) {
        List<String> phoneNumbers = Arrays.stream(
                        createClientDto.getPhoneNumbers().split(","))
                .filter(phone -> !phone.isBlank())
                .toList();
        ClientDto client = new ClientDto(null, createClientDto.getName(), createClientDto.getStreet(), phoneNumbers);
        clientService.save(client);
        return new RedirectView("/clients", true);
    }
}
