package com.gic.repository;

import com.gic.model.Instrument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstrumentRepository extends MongoRepository<Instrument, String> {
    List<Instrument> findByInstrumentGroupAndInstrumentAndSettlementCCYAndTradeCCYAndRiskCountryAndExchangeAndDepartment(
            String instrumentGroup, String instrument, String settlementCCY, String tradeCCY, String riskCountry, String exchange, String department);

    @Query(value="{}", fields="{ '_id' : 1}")
    List<String> findAllIds();
}