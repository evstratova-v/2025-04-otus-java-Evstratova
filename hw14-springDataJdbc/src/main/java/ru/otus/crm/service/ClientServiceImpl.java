package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.core.repository.ClientRepository;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.dto.ClientDto;
import ru.otus.crm.model.Client;

@Service
public class ClientServiceImpl implements ClientService {
    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientRepository clientRepository;
    private final TransactionManager transactionManager;

    public ClientServiceImpl(ClientRepository clientRepository, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDto save(ClientDto clientDto) {
        Client client = ClientDto.toEntity(clientDto);
        var savedClientEntity = transactionManager.doInTransaction(() -> {
            Client savedClient = clientRepository.save(client);
            log.info("save client: {}", savedClient);
            return savedClient;
        });
        return ClientDto.toDto(savedClientEntity);
    }

    @Override
    public Optional<ClientDto> findById(long id) {
        var clientOptionalEntity = transactionManager.doInReadOnlyTransaction(() -> {
            var clientOptional = clientRepository.findById(id);
            log.info("client: {}", clientOptional);
            return clientOptional;
        });
        return clientOptionalEntity.map(ClientDto::toDto);
    }

    @Override
    public List<ClientDto> findAll() {
        List<Client> clients = transactionManager.doInReadOnlyTransaction(() -> {
            var clientList = clientRepository.findAll();
            log.info("clientList:{}", clientList);
            return clientList;
        });
        return clients.stream().map(ClientDto::toDto).toList();
    }
}
