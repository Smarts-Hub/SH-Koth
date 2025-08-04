package dev.smartshub.shkoth.api.model.builder;

public interface Builder<T, K> {
    T build(K k);
}
