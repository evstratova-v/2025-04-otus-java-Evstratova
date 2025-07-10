package ru.otus.atm;

import java.util.List;

public interface Atm {

    int getBalance();

    void deposit(List<Banknote> banknotes);

    List<Banknote> withdraw(int amount);
}
