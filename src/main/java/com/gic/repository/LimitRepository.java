package com.gic.repository;

import com.gic.model.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LimitRepository extends MongoRepository<Limit, String> {
    Limit findByCounterparty(String counterparty);
}