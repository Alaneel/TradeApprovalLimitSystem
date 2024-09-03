package com.gic.service;

import com.gic.model.ApprovalRequest;
import com.gic.model.Instrument;
import com.gic.model.TradeRequest;
import com.gic.model.TradeHistory;
import com.gic.repository.TradeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TradeService {

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private LimitService limitService;

    @Autowired
    private TradeHistoryRepository tradeHistoryRepository;

    public String processTrade(TradeRequest tradeRequest) {
        Optional<Instrument> instrumentOpt = instrumentService.findInstrument(tradeRequest.getInstrumentId());

        if (instrumentOpt.isEmpty()) {
            if (!tradeRequest.isConfirmed()) {
                return "Instrument not found. Please confirm if you want to proceed with approval.";
            } else {
                ApprovalRequest approvalRequest = new ApprovalRequest();
                approvalRequest.setInstrumentId(tradeRequest.getInstrumentId());
                approvalRequest.setStatus("PENDING");
                approvalService.createApprovalRequest(approvalRequest);
                return "Approval request submitted for the new instrument.";
            }
        }

        if (limitService.checkLimit(tradeRequest.getCounterparty(), tradeRequest.getAmount())) {
            boolean tradeExecuted = executeTradeOnExternalPlatform(tradeRequest);

            if (tradeExecuted) {
                limitService.updateLimit(tradeRequest.getCounterparty(), tradeRequest.getAmount());
                saveTrade(tradeRequest, "EXECUTED");
                return "Trade executed successfully.";
            } else {
                saveTrade(tradeRequest, "FAILED");
                return "Trade execution failed on the external platform.";
            }
        } else {
            saveTrade(tradeRequest, "REJECTED");
            return "Trade rejected. Insufficient limit for the counterparty.";
        }
    }

    private void saveTrade(TradeRequest tradeRequest, String status) {
        TradeHistory tradeHistory = new TradeHistory();
        tradeHistory.setInstrumentId(tradeRequest.getInstrumentId());
        tradeHistory.setCounterparty(tradeRequest.getCounterparty());
        tradeHistory.setAmount(tradeRequest.getAmount());
        tradeHistory.setTimestamp(LocalDateTime.now());
        tradeHistory.setStatus(status);
        tradeHistoryRepository.save(tradeHistory);
    }

    private boolean executeTradeOnExternalPlatform(TradeRequest tradeRequest) {
        return Math.random() < 0.9; // 90% success rate for demonstration
    }
}