package ru.otus.atm;

public enum Denomination implements Comparable<Denomination> {
    D50(50),
    D100(100),
    D200(200),
    D500(500),
    D1000(1000),
    D2000(2000),
    D5000(5000);

    private final int value;

    Denomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
