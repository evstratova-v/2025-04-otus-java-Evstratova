package ru.otus.crm.model;

import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("client")
public class Client implements Persistable<Long> {

    @Id
    @Column("id")
    private final Long id;

    @Column("name")
    private final String name;

    @MappedCollection(idColumn = "client_id")
    private final Address address;

    @MappedCollection(idColumn = "client_id", keyColumn = "order_column")
    private final List<Phone> phones;

    @Transient
    private final boolean isNew;

    @PersistenceCreator
    private Client(Long id, String name, Address address, @NonNull List<Phone> phones) {
        this(id, name, address, phones, false);
    }

    public Client(Long id, String name, Address address, @NonNull List<Phone> phones, boolean isNew) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones;
        this.isNew = isNew;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + ", address=" + address + ", phones=" + phones + '}';
    }
}
