package ru.otus.atm;

public record Banknote(Denomination denomination) {

    public int getValue() {
        return denomination.getValue();
    }

    @Override
    public String toString() {
        return denomination.toString();
    }
}
