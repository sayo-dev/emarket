package org.example.e_market.services;

public interface BaseService<I, O> {

    void create(final I request);

    void update(final Long id, final I request);

    O findById(final Long id);

    void delete(final Long id);
}
