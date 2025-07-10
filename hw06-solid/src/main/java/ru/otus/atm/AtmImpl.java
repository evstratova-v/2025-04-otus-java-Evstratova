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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmImpl implements Atm {

    private final Logger log = LoggerFactory.getLogger(AtmImpl.class);

    private final NavigableMap<Denomination, Cell> cells;

    public AtmImpl() {
        this.cells = new TreeMap<>(Collections.reverseOrder());
        Arrays.stream(Denomination.values()).forEach(denomination -> cells.put(denomination, new Cell(denomination)));
    }

    public AtmImpl(List<Banknote> banknotes) {
        this.cells = new TreeMap<>(Collections.reverseOrder());
        Arrays.stream(Denomination.values()).forEach(denomination -> cells.put(denomination, new Cell(denomination)));
        for (Banknote banknote : banknotes) {
            cells.get(banknote.denomination()).depositBanknote(banknote);
        }
    }

    @Override
    public int getBalance() {
        int balance = sumBanknotes();
        log.info("Сумма остатка денежных средств: {}", balance);
        return balance;
    }

    @Override
    public void deposit(List<Banknote> banknotes) {
        for (Banknote banknote : banknotes) {
            cells.get(banknote.denomination()).depositBanknote(banknote);
        }
        log.atInfo()
                .setMessage("Добавлены банкноты: {}")
                .addArgument(() ->
                        banknotes.stream().collect(Collectors.groupingBy(banknote -> banknote, Collectors.counting())))
                .log();
    }

    @Override
    public List<Banknote> withdraw(int amount) {
        log.info("Запрошена сумма: {}", amount);
        validateAmount(amount);
        Map<Denomination, Integer> availableBanknotes = cells.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, dsEntry -> dsEntry.getValue().size()));

        Map<Denomination, Integer> optimalCombination = findOptimalCombination(amount, availableBanknotes);

        List<Banknote> banknotes = new ArrayList<>();
        for (Map.Entry<Denomination, Integer> entry : optimalCombination.entrySet()) {
            banknotes.addAll(cells.get((entry.getKey())).withdrawBanknotes(entry.getValue()));
        }
        log.atInfo()
                .setMessage("Выданы банкноты: {}, для суммы: {}")
                .addArgument(() ->
                        banknotes.stream().collect(Collectors.groupingBy(banknote -> banknote, Collectors.counting())))
                .addArgument(amount)
                .log();
        return banknotes;
    }

    private Map<Denomination, Integer> findOptimalCombination(
            int amount, Map<Denomination, Integer> availableBanknotes) {

        // countArr[remaining] хранит минимальное количество банкнот для суммы remaining
        int[] countArr = new int[amount + 1];
        Arrays.fill(countArr, Integer.MAX_VALUE);
        countArr[0] = 0;

        // хранит комбинации банкнот для суммы remaining
        Map<Integer, Map<Denomination, Integer>> combinations = new HashMap<>();
        combinations.put(0, new EnumMap<>(Denomination.class));

        for (Denomination denomination : cells.navigableKeySet()) {
            int coin = denomination.getValue();
            int available = availableBanknotes.get(denomination);
            if (available == 0) continue;

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

    private Map<Denomination, Integer> getOptimalCombination(
            int[] dp, Map<Integer, Map<Denomination, Integer>> combinations, int amount) {
        if (dp[amount] != Integer.MAX_VALUE) {
            return combinations.get(amount);
        } else {
            throw new AtmException("Невозможно выдать запрошенную сумму: %d".formatted(amount));
        }
    }

    private void validateAmount(int amount) {
        if (amount <= 0) {
            throw new AtmException("Укажите сумму больше нуля");
        }
        if (sumBanknotes() < amount) {
            throw new AtmException("В банкомате недостаточно денежных средств");
        }

        Denomination minAvailableDenomination = cells.values().stream()
                .filter(cell -> cell.size() > 0)
                .map(Cell::getDenomination)
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new AtmException("Не найден минимальный доступный номинал"));

        if (amount % minAvailableDenomination.getValue() > 0) {
            throw new AtmException("Невозможно выдать запрошенную сумму, минимальный доступный номинал: %s"
                    .formatted(minAvailableDenomination));
        }
    }

    private int sumBanknotes() {
        return cells.values().stream()
                .flatMap(cell -> cell.getBanknotes().stream())
                .map(Banknote::getValue)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
