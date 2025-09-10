package ru.otus.crm.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

@Getter
@AllArgsConstructor
public class ClientDto {

    private Long id;

    private String name;

    private String street;

    private List<String> phoneNumbers;

    public static ClientDto toDto(Client client) {
        String street = client.getAddress() != null ? client.getAddress().street() : null;
        List<String> phoneNumbers = client.getPhones() != null
                ? client.getPhones().stream().map(Phone::number).toList()
                : new ArrayList<>();
        return new ClientDto(client.getId(), client.getName(), street, phoneNumbers);
    }

    public static Client toEntity(ClientDto clientDto) {
        boolean isNew = clientDto.getId() == null;
        Long id = isNew ? System.currentTimeMillis() : clientDto.getId();
        Address address = clientDto.getStreet() != null ? new Address(null, clientDto.getStreet(), id) : null;
        List<Phone> phones = clientDto.getPhoneNumbers() != null
                ? clientDto.getPhoneNumbers().stream()
                        .map(number -> new Phone(null, number, id, null))
                        .toList()
                : new ArrayList<>();
        return new Client(id, clientDto.getName(), address, phones, isNew);
    }
}
