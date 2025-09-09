package ru.otus.core.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.crm.model.Client;

public interface ClientRepository extends ListCrudRepository<Client, Long> {}
