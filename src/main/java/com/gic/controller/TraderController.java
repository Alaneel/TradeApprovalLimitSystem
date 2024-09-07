package com.gic.controller;

import com.gic.model.*;
import com.gic.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trader")
public class TraderController {

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    private LimitService limitService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private TradeService tradeService;

    @PostMapping("/verify-instrument")
    public ResponseEntity<?> verifyInstrument(@RequestBody InstrumentVerificationRequest request) {
        InstrumentVerificationResult result = instrumentService.verifyInstrument(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/approval-request")
    public ResponseEntity<ApprovalRequest> createApprovalRequest(@RequestBody InstrumentVerificationRequest request) {
        ApprovalRequest approvalRequest = approvalService.createApprovalRequest(request);
        return ResponseEntity.ok(approvalRequest);
    }

    @GetMapping("/limit/{counterparty}/{instrumentGroup}")
    public ResponseEntity<Limit> getAvailableLimit(@PathVariable String counterparty, @PathVariable String instrumentGroup) {
        Limit limit = limitService.getAvailableLimit(counterparty, instrumentGroup);
        return ResponseEntity.ok(limit);
    }

    @PostMapping("/trade")
    public ResponseEntity<TradeResult> executeTrade(@RequestBody TradeRequest tradeRequest) {
        TradeResult result = tradeService.processTrade(tradeRequest);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trades")
    public ResponseEntity<List<TradeHistory>> getTradeHistory() {
        List<TradeHistory> tradeHistory = tradeService.getTradeHistory();
        return ResponseEntity.ok(tradeHistory);
    }
}