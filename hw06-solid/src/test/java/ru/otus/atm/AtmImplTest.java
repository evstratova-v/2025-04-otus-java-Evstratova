package ru.otus.atm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.otus.atm.Denomination.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тесты эмулятора банкомата")
class AtmImplTest {

    private List<Banknote> banknotes;

    @BeforeEach
    void setUp() {
        banknotes = new ArrayList<>();
        for (Denomination denomination : Denomination.values()) {
            for (int i = 0; i <= 1000; i++) {
                banknotes.add(new Banknote(denomination));
            }
        }
    }

    @DisplayName("Пополнение пустого банкомата банкнотами разных номиналов")
    @Test
    void testEmptyAtm() {
        Atm atm = new AtmImpl();
        assertEquals(0, atm.getBalance());

        atm.deposit(banknotes);
        int balanceAfter = atm.getBalance();

        assertEquals(8_858_850, balanceAfter);
    }

    @DisplayName("Пополнение непустого банкомата банкнотами разных номиналов")
    @Test
    void testDeposit() {
        Atm atm = new AtmImpl(banknotes);

        int balanceBefore = atm.getBalance();
        atm.deposit(List.of(new Banknote(D2000), new Banknote(D2000), new Banknote(D50)));
        int balanceAfter = atm.getBalance();

        assertEquals(balanceBefore + 4050, balanceAfter);
    }

    @DisplayName("Выдача запрошенной суммы")
    @Test
    void testWithdraw() {
        Atm atm = new AtmImpl(banknotes);

        int balanceBefore = atm.getBalance();
        atm.withdraw(1_515_750);
        int balanceAfter = atm.getBalance();

        assertEquals(balanceBefore - 1_515_750, balanceAfter);
    }

    @DisplayName("Выдача запрошенной суммы минимальным количеством банкнот")
    @Test
    void testWithdrawOptimal() {
        banknotes = new ArrayList<>();
        banknotes.addAll(Collections.nCopies(1, new Banknote(D5000)));
        banknotes.addAll(Collections.nCopies(3, new Banknote(D2000)));
        banknotes.addAll(Collections.nCopies(10, new Banknote(D100)));
        AtmImpl atm = new AtmImpl(banknotes);

        int balanceBefore = atm.getBalance();
        List<Banknote> withdrawBanknotes = atm.withdraw(6000);
        int balanceAfter = atm.getBalance();

        assertEquals(balanceBefore - 6000, balanceAfter);
        assertEquals(3, withdrawBanknotes.size());
        assertThat(withdrawBanknotes)
                .allMatch(banknote -> banknote.denomination().equals(D2000));
    }

    @DisplayName("Ошибка при запросе суммы больше остатка денежных средств")
    @Test
    void testWithdrawTooBigAmount() {
        Atm atm = new AtmImpl(banknotes);

        int balanceBefore = atm.getBalance();
        AtmException exception = assertThrows(AtmException.class, () -> atm.withdraw(15_150_000));
        assertEquals("В банкомате недостаточно денежных средств", exception.getMessage());
        int balanceAfter = atm.getBalance();

        assertEquals(balanceBefore, balanceAfter);
    }

    @DisplayName("Ошибка при запросе суммы, которая требует номинал меньше доступного")
    @Test
    void testWithdrawTooSmallAmount() {
        Atm atm = new AtmImpl(List.of(new Banknote(D5000), new Banknote(D500)));

        int balanceBefore = atm.getBalance();
        AtmException exception = assertThrows(AtmException.class, () -> atm.withdraw(5100));
        assertEquals(
                "Невозможно выдать запрошенную сумму, минимальный доступный номинал: %s".formatted(D500),
                exception.getMessage());
        int balanceAfter = atm.getBalance();

        assertEquals(balanceBefore, balanceAfter);
    }

    @DisplayName("Ошибка при запросе суммы равной нулю")
    @Test
    void testWithdrawZeroAmount() {
        Atm atm = new AtmImpl(banknotes);

        int balanceBefore = atm.getBalance();
        AtmException exception = assertThrows(AtmException.class, () -> atm.withdraw(0));
        assertEquals("Укажите сумму больше нуля", exception.getMessage());
        int balanceAfter = atm.getBalance();

        assertEquals(balanceBefore, balanceAfter);
    }
}
