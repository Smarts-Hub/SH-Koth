package dev.smartshub.shkoth.api.builder;

public interface Builder<T, K> {
    T build(K k);
}
