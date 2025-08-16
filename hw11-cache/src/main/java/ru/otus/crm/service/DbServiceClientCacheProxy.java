package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import ru.otus.cachehw.HwCache;
import ru.otus.crm.model.Client;

@RequiredArgsConstructor
public class DbServiceClientCacheProxy implements DBServiceClient {

    private final DBServiceClient dbServiceClient;

    private final HwCache<Long, Client> cache;

    @Override
    public Client saveClient(Client client) {
        var savedClient = dbServiceClient.saveClient(client);
        cache.put(savedClient.getId(), savedClient.clone());
        return savedClient;
    }

    @Override
    public Optional<Client> getClient(long id) {
        var cachedClient = cache.get(id);
        if (cachedClient != null) {
            return Optional.of(cachedClient.clone());
        }
        var optionalClient = dbServiceClient.getClient(id);
        optionalClient.ifPresent(client -> cache.put(id, client.clone()));
        return optionalClient;
    }

    @Override
    public List<Client> findAll() {
        var clients = dbServiceClient.findAll();
        for (var client : clients) {
            cache.put(client.getId(), client.clone());
        }
        return clients;
    }
}
