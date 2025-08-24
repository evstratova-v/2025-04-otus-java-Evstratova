package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.dto.ClientDto;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
    }

    @Override
    public ClientDto saveClient(ClientDto clientDto) {
        Client client = ClientDto.toEntity(clientDto);
        var savedClientEntity = transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                var savedClient = clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);
                return savedClient;
            }
            var savedClient = clientDataTemplate.update(session, clientCloned);
            log.info("updated client: {}", savedClient);
            return savedClient;
        });
        return ClientDto.toDto(savedClientEntity);
    }

    @Override
    public Optional<ClientDto> getClient(long id) {
        var clientOptionalEntity = transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            log.info("client: {}", clientOptional);
            return clientOptional.map(client -> {
                client.setAddress(Hibernate.unproxy(client.getAddress(), Address.class));
                return client;
            });
        });
        return clientOptionalEntity.map(ClientDto::toDto);
    }

    @Override
    public List<ClientDto> findAll() {
        List<Client> clients = transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientList;
        });
        return clients.stream().map(ClientDto::toDto).toList();
    }
}
