package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import ru.otus.cachehw.HwCache;
import ru.otus.crm.dto.ClientDto;

@RequiredArgsConstructor
public class DbServiceClientCacheProxy implements DBServiceClient {

    private final DBServiceClient dbServiceClient;

    private final HwCache<Long, ClientDto> cache;

    @Override
    public ClientDto saveClient(ClientDto clientDto) {
        var savedClient = dbServiceClient.saveClient(clientDto);
        cache.put(savedClient.getId(), savedClient);
        return savedClient;
    }

    @Override
    public Optional<ClientDto> getClient(long id) {
        var cachedClient = cache.get(id);
        if (cachedClient != null) {
            return Optional.of(cachedClient);
        }
        var optionalClient = dbServiceClient.getClient(id);
        optionalClient.ifPresent(client -> cache.put(id, client));
        return optionalClient;
    }

    @Override
    public List<ClientDto> findAll() {
        var clients = dbServiceClient.findAll();
        for (var client : clients) {
            cache.put(client.getId(), client);
        }
        return clients;
    }
}
