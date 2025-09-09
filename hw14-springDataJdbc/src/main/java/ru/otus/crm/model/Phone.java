package ru.otus.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "phone")
public record Phone(
        @Id @Column("id") Long id,
        @Column("number") String number,
        @Column("client_id") Long clientId,
        @Column("order_column") Integer orderColumn) {}
