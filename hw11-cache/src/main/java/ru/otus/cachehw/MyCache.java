package ru.otus.cachehw;

import static ru.otus.cachehw.CacheActions.GET;
import static ru.otus.cachehw.CacheActions.PUT;
import static ru.otus.cachehw.CacheActions.REMOVE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyCache<K, V> implements HwCache<K, V> {

    private final Map<K, V> cache;

    private final List<HwListener<K, V>> listeners;

    public MyCache() {
        cache = new WeakHashMap<>();
        listeners = new ArrayList<>();
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notify(key, value, PUT);
    }

    @Override
    public void remove(K key) {
        V value = cache.remove(key);
        notify(key, value, REMOVE);
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        notify(key, value, GET);
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notify(K key, V value, CacheActions action) {
        for (var listener : listeners) {
            try {
                listener.notify(key, value, action.toString());
            } catch (Exception e) {
                log.error("error notify, key: {}, value: {}, action: {}", key, value, action, e);
            }
        }
    }
}
