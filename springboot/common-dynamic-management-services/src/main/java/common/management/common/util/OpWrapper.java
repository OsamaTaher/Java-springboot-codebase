package common.management.common.util;

public record OpWrapper<T>(
    int status,
    T data
) { }
