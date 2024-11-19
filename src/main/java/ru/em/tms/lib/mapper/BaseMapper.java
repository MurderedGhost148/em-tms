package ru.em.tms.lib.mapper;

public interface BaseMapper<S, D> {
    D sourceToDestination(S source);
    S destinationToSource(D destination);
}
