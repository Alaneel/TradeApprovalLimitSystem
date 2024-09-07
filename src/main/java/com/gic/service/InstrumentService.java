package com.gic.service;

import com.gic.model.Instrument;
import com.gic.model.InstrumentVerificationRequest;
import com.gic.model.InstrumentVerificationResult;
import com.gic.repository.InstrumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InstrumentService {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Cacheable(value = "instruments", key = "#id")
    public InstrumentVerificationResult verifyInstrument(String id) {
        log.debug("Verifying instrument with id: {}", id);

        Optional<Instrument> instrumentOpt = instrumentRepository.findById(id);
        InstrumentVerificationResult result = new InstrumentVerificationResult();

        if (instrumentOpt.isPresent()) {
            Instrument instrument = instrumentOpt.get();
            result.setValid(true);
            result.setApprovedForDepartment(true); // Assuming all found instruments are approved
            result.setInstrument(instrument);
            result.setMessage("Instrument found and verified.");
        } else {
            result.setMessage("Instrument not found. Would you like to submit an approval request?");
        }

        log.debug("Verification result: {}", result);
        return result;
    }

    public List<String> getAllInstrumentIds() {
        return instrumentRepository.findAllIds();
    }

    @Cacheable(value = "instruments", key = "#request")
    public InstrumentVerificationResult verifyInstrument(InstrumentVerificationRequest request) {
        log.debug("Verifying instrument for request: {}", request);

        List<Instrument> matchingInstruments = instrumentRepository.findByInstrumentGroupAndInstrumentAndSettlementCCYAndTradeCCYAndRiskCountryAndExchangeAndDepartment(
                request.getInstrumentGroup(),
                request.getInstrument(),
                request.getSettlementCurrency(),
                request.getTradeCurrency(),
                request.getCountry(),
                request.getExchange(),
                request.getDepartment()
        );

        InstrumentVerificationResult result = new InstrumentVerificationResult();
        if (!matchingInstruments.isEmpty()) {
            result.setValid(true);
            result.setApprovedForDepartment(true);
            result.setInstrument(matchingInstruments.get(0));
            result.setMessage("Instrument found and verified.");
        } else {
            result.setMessage("No matching instruments found. Would you like to submit an approval request?");
        }

        log.debug("Verification result: {}", result);
        return result;
    }
}