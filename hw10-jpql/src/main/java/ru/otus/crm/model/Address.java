package ru.otus.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "street")
    private String street;

    public Address(String street) {
        this.street = street;
    }

    public Address(Long id, String street) {
        this.id = id;
        this.street = street;
    }

    @Override
    public String toString() {
        return "Address{" + "id=" + id + ", street='" + street + '\'' + '}';
    }
}
