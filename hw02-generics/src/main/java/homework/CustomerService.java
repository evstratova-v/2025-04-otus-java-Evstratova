package homework;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public class CustomerService {

    private final NavigableMap<Customer, String> customersData =
            new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        var optionalEntry = Optional.ofNullable(customersData.firstEntry());
        return optionalEntry
                .map(entry -> Map.entry(
                        new Customer(
                                entry.getKey().getId(),
                                entry.getKey().getName(),
                                entry.getKey().getScores()),
                        entry.getValue()))
                .orElse(null);
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        var optionalEntry = Optional.ofNullable(customersData.higherEntry(customer));
        return optionalEntry
                .map(entry -> Map.entry(
                        new Customer(
                                entry.getKey().getId(),
                                entry.getKey().getName(),
                                entry.getKey().getScores()),
                        entry.getValue()))
                .orElse(null);
    }

    public void add(Customer customer, String data) {
        customersData.put(customer, data);
    }
}
