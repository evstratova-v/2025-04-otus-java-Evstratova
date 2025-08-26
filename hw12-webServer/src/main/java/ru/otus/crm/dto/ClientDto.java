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
        String street = client.getAddress() != null ? client.getAddress().getStreet() : null;
        List<String> phoneNumbers = client.getPhones() != null
                ? client.getPhones().stream().map(Phone::getNumber).toList()
                : new ArrayList<>();
        return new ClientDto(client.getId(), client.getName(), street, phoneNumbers);
    }

    public static Client toEntity(ClientDto clientDto) {
        Address address = clientDto.getStreet() != null ? new Address(clientDto.getStreet()) : null;
        List<Phone> phones = clientDto.getPhoneNumbers() != null
                ? clientDto.getPhoneNumbers().stream().map(Phone::new).toList()
                : new ArrayList<>();
        return new Client(clientDto.getId(), clientDto.getName(), address, phones);
    }
}
