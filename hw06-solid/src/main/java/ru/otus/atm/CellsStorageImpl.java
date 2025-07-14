package ru.otus.atm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CellsStorageImpl implements CellsStorage {

    private final NavigableMap<Denomination, Cell> cells;

    public CellsStorageImpl(List<Banknote> banknotes) {
        this.cells = new TreeMap<>(Collections.reverseOrder());
        Arrays.stream(Denomination.values()).forEach(denomination -> cells.put(denomination, new Cell(denomination)));
        for (Banknote banknote : banknotes) {
            cells.get(banknote.denomination()).depositBanknote(banknote);
        }
    }

    @Override
    public void depositBanknotes(List<Banknote> banknotes) {
        for (Banknote banknote : banknotes) {
            cells.get(banknote.denomination()).depositBanknote(banknote);
        }
    }

    @Override
    public Map<Denomination, Integer> findOptimalCombination(int amount) {
        NavigableMap<Denomination, Integer> availableBanknotes = getAvailableBanknotes();

        // countArr[remaining] хранит минимальное количество банкнот для суммы remaining
        int[] countArr = new int[amount + 1];
        Arrays.fill(countArr, Integer.MAX_VALUE);
        countArr[0] = 0;

        // хранит комбинации банкнот для суммы remaining
        Map<Integer, Map<Denomination, Integer>> combinations = new HashMap<>();
        combinations.put(0, new EnumMap<>(Denomination.class));

        for (Denomination denomination : availableBanknotes.navigableKeySet()) {
            int coin = denomination.getValue();
            int available = availableBanknotes.get(denomination);

            for (int remaining = amount; remaining >= coin; remaining--) {
                for (int count = 1; count <= available && count * coin <= remaining; count++) {
                    int newBanknoteCount = countArr[remaining - count * coin] + count;
                    if (countArr[remaining - count * coin] != Integer.MAX_VALUE
                            && newBanknoteCount < countArr[remaining]) {

                        countArr[remaining] = newBanknoteCount;

                        Map<Denomination, Integer> newComb = new EnumMap<>(combinations.get(remaining - count * coin));
                        newComb.put(denomination, newComb.getOrDefault(denomination, 0) + count);
                        combinations.put(remaining, newComb);
                    }
                }
            }
        }
        return getOptimalCombination(countArr, combinations, amount);
    }

    @Override
    public List<Banknote> withdrawBanknotes(Map<Denomination, Integer> optimalCombination) {
        List<Banknote> banknotes = new ArrayList<>();
        for (Map.Entry<Denomination, Integer> entry : optimalCombination.entrySet()) {
            banknotes.addAll(cells.get((entry.getKey())).withdrawBanknotes(entry.getValue()));
        }
        return banknotes;
    }

    @Override
    public int sumBanknotes() {
        return cells.values().stream()
                .flatMap(cell -> cell.getBanknotes().stream())
                .map(Banknote::getValue)
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public Denomination getMinAvailableDenomination() {
        return cells.values().stream()
                .filter(cell -> cell.size() > 0)
                .map(Cell::getDenomination)
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new AtmException("Не найден минимальный доступный номинал"));
    }

    private NavigableMap<Denomination, Integer> getAvailableBanknotes() {
        return new TreeMap<>(cells.entrySet().stream()
                .filter(cell -> cell.getValue().size() > 0)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, dsEntry -> dsEntry.getValue().size())));
    }

    private Map<Denomination, Integer> getOptimalCombination(
            int[] dp, Map<Integer, Map<Denomination, Integer>> combinations, int amount) {
        if (dp[amount] != Integer.MAX_VALUE) {
            return combinations.get(amount);
        } else {
            throw new AtmException("Невозможно выдать запрошенную сумму: %d".formatted(amount));
        }
    }
}
