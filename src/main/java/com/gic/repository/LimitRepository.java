package com.gic.repository;

import com.gic.model.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LimitRepository extends MongoRepository<Limit, String> {
    List<Limit> findByCounterpartyAndInstrumentGroup(String counterparty, String instrumentGroup);

    // Option: Use this method if you want to get only the first result
    // Limit findFirstByCounterpartyAndInstrumentGroup(String counterparty, String instrumentGroup);
}