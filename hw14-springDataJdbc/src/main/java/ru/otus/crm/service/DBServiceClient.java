package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import ru.otus.crm.dto.ClientDto;

public interface DBServiceClient {

    ClientDto saveClient(ClientDto clientDto);

    Optional<ClientDto> getClient(long id);

    List<ClientDto> findAll();
}
