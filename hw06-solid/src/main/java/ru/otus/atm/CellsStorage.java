package ru.otus.atm;

import java.util.List;
import java.util.Map;

public interface CellsStorage {

    void depositBanknotes(List<Banknote> banknotes);

    Map<Denomination, Integer> findOptimalCombination(int amount);

    List<Banknote> withdrawBanknotes(Map<Denomination, Integer> optimalCombination);

    int sumBanknotes();

    Denomination getMinAvailableDenomination();
}
