package com.gic.repository;

import com.gic.model.TradeHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TradeHistoryRepository extends MongoRepository<TradeHistory, String> {
}