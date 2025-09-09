package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import ru.otus.crm.dto.ClientDto;

public interface ClientService {

    ClientDto save(ClientDto clientDto);

    Optional<ClientDto> findById(long id);

    List<ClientDto> findAll();
}
