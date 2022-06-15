package com.alexsobiek.nexus.inject.dependency;

import com.alexsobiek.nexus.inject.annotation.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class DependencyInfo {
    private final Class<?> type;
    private final String identifier;

    public DependencyInfo(Class<?> type) {
        this(type, Inject.EMPTY_IDENTIFIER);
    }
}
