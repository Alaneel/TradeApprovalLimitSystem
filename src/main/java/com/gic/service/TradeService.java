package com.gic.service;

import com.gic.model.*;
import com.gic.repository.TradeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TradeService {

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    private LimitService limitService;

    @Autowired
    private TradeHistoryRepository tradeHistoryRepository;

    @Transactional
    public TradeResult processTrade(TradeRequest tradeRequest) {
        InstrumentVerificationResult verificationResult = instrumentService.verifyInstrument(tradeRequest.getInstrumentVerificationRequest());

        if (!verificationResult.isValid()) {
            return new TradeResult("REJECTED", "Invalid instrument combination");
        }

        if (!verificationResult.isApprovedForDepartment()) {
            return new TradeResult("REJECTED", "Instrument not approved for your department");
        }

        String instrumentGroup = verificationResult.getInstrument().getInstrumentGroup();
        boolean limitAvailable = limitService.checkAndReserveLimit(tradeRequest.getCounterparty(), instrumentGroup, tradeRequest.getAmount());

        if (!limitAvailable) {
            return new TradeResult("REJECTED", "Insufficient limit for the counterparty and instrument group");
        }

        boolean tradeExecuted = executeTradeOnExternalPlatform(tradeRequest);

        if (tradeExecuted) {
            limitService.confirmLimitUsage(tradeRequest.getCounterparty(), instrumentGroup, tradeRequest.getAmount());
            saveTrade(tradeRequest, "EXECUTED");
            return new TradeResult("EXECUTED", "Trade executed successfully");
        } else {
            limitService.releaseLimitReservation(tradeRequest.getCounterparty(), instrumentGroup, tradeRequest.getAmount());
            saveTrade(tradeRequest, "FAILED");
            return new TradeResult("FAILED", "Trade execution failed on the external platform");
        }
    }

    private void saveTrade(TradeRequest tradeRequest, String status) {
        TradeHistory tradeHistory = new TradeHistory();
        tradeHistory.setInstrumentVerificationRequest(tradeRequest.getInstrumentVerificationRequest());
        tradeHistory.setCounterparty(tradeRequest.getCounterparty());
        tradeHistory.setAmount(tradeRequest.getAmount());
        tradeHistory.setTimestamp(LocalDateTime.now());
        tradeHistory.setStatus(status);
        tradeHistoryRepository.save(tradeHistory);
    }

    private boolean executeTradeOnExternalPlatform(TradeRequest tradeRequest) {
        // Simulate external trade execution
        try {
            Thread.sleep(500); // Simulate network latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Math.random() < 0.9; // 90% success rate for demonstration
    }

    public List<TradeHistory> getTradeHistory() {
        return tradeHistoryRepository.findAll();
    }
}