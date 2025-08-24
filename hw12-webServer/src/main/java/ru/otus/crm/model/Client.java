package ru.otus.crm.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
@NamedEntityGraph(
        name = "client-address-entity-graph",
        attributeNodes = {@NamedAttributeNode("address")})
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @BatchSize(size = 5)
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Phone> phones;

    @SuppressWarnings("this-escape")
    public Client(String name, Address address, @NonNull List<Phone> phones) {
        this.id = null;
        this.name = name;
        this.address = address;
        this.phones = phones;
        for (Phone phone : phones) {
            phone.setClient(this);
        }
    }

    public Client(Long id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    @SuppressWarnings("this-escape")
    public Client(Long id, String name, Address address, @NonNull List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones;
        for (Phone phone : phones) {
            phone.setClient(this);
        }
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Address cloneAddress = address != null ? new Address(address.getId(), address.getStreet()) : null;
        List<Phone> clonePhones;
        if (phones != null) {
            clonePhones = phones.stream()
                    .map(phone -> new Phone(phone.getId(), phone.getNumber()))
                    .toList();
            return new Client(this.id, this.name, cloneAddress, clonePhones);
        } else {
            return new Client(this.id, this.name, cloneAddress);
        }
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + ", address=" + address + ", phones=" + phones + '}';
    }
}
