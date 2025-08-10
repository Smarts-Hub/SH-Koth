package dev.smartshub.shkoth.api.builder.mapper;

public interface Mapper<T, K> {
    T map(K k);
}
