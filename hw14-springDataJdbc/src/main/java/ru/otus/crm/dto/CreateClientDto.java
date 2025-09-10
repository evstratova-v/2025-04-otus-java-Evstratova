package ru.otus.crm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateClientDto {

    private String name;

    private String street;

    private String phoneNumbers;
}
