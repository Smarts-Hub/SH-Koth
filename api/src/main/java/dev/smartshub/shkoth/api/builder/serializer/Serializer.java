package dev.smartshub.shkoth.api.builder.serializer;

public interface Serializer<T, K> {
    T serialize(K k);
}