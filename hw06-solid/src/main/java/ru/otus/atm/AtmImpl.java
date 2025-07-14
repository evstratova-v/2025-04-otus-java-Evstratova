package ru.otus.atm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmImpl implements Atm {

    private final Logger log = LoggerFactory.getLogger(AtmImpl.class);

    private final CellsStorage cellsStorage;

    public AtmImpl(List<Banknote> banknotes) {
        this.cellsStorage = new CellsStorageImpl(banknotes);
    }

    @Override
    public int getBalance() {
        int balance = cellsStorage.sumBanknotes();
        log.info("Сумма остатка денежных средств: {}", balance);
        return balance;
    }

    @Override
    public void deposit(List<Banknote> banknotes) {
        cellsStorage.depositBanknotes(banknotes);
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

        Map<Denomination, Integer> optimalCombination = cellsStorage.findOptimalCombination(amount);

        List<Banknote> banknotes = cellsStorage.withdrawBanknotes(optimalCombination);
        log.atInfo()
                .setMessage("Выданы банкноты: {}, для суммы: {}")
                .addArgument(() ->
                        banknotes.stream().collect(Collectors.groupingBy(banknote -> banknote, Collectors.counting())))
                .addArgument(amount)
                .log();
        return banknotes;
    }

    private void validateAmount(int amount) {
        if (amount <= 0) {
            throw new AtmException("Укажите сумму больше нуля");
        }
        if (cellsStorage.sumBanknotes() < amount) {
            throw new AtmException("В банкомате недостаточно денежных средств");
        }

        Denomination minAvailableDenomination = cellsStorage.getMinAvailableDenomination();
        if (amount % minAvailableDenomination.getValue() > 0) {
            throw new AtmException("Невозможно выдать запрошенную сумму, минимальный доступный номинал: %s"
                    .formatted(minAvailableDenomination));
        }
    }
}
