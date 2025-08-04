package dev.smartshub.shkoth.api.model.builder.mapper;

public interface Mapper<T, K> {
    T map(K k);
}
