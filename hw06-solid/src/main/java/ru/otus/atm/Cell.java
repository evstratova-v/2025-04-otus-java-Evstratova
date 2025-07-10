package ru.otus.atm;

import java.util.ArrayList;
import java.util.List;

public class Cell {

    private final Denomination denomination;

    private final List<Banknote> banknotes;

    private Cell() {
        throw new UnsupportedOperationException("У ячейки должен быть задан номинал банкнот, которые она хранит");
    }

    public Cell(Denomination denomination) {
        this.denomination = denomination;
        this.banknotes = new ArrayList<>();
    }

    public List<Banknote> getBanknotes() {
        return banknotes;
    }

    public void depositBanknote(Banknote banknote) {
        if (banknote.denomination() != denomination) {
            throw new UnsupportedOperationException(
                    "Нельзя добавить банкноты с номиналом, отличным от: %s".formatted(denomination));
        }
        banknotes.add(banknote);
    }

    public List<Banknote> withdrawBanknotes(int count) {
        int fromIndex = banknotes.size() - count;
        int toIndex = banknotes.size();

        List<Banknote> subListBanknotes = banknotes.subList(fromIndex, toIndex);
        List<Banknote> withdrawBanknotes = new ArrayList<>(subListBanknotes);
        subListBanknotes.clear();

        return withdrawBanknotes;
    }

    public Denomination getDenomination() {
        return denomination;
    }

    public int size() {
        return banknotes.size();
    }
}
