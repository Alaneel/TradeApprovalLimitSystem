package com.gic.controller;

import com.gic.model.Instrument;
import com.gic.model.ApprovalRequest;
import com.gic.model.TradeRequest;
import com.gic.service.InstrumentService;
import com.gic.service.LimitService;
import com.gic.service.ApprovalService;
import com.gic.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping("/instrument/{id}")
    public ResponseEntity<?> getInstrument(@PathVariable String id) {
        Optional<Instrument> instrumentOpt = instrumentService.findInstrument(id);
        if (instrumentOpt.isPresent()) {
            return ResponseEntity.ok(instrumentOpt.get());
        } else {
            return ResponseEntity.ok("Instrument not found. Would you like to submit an approval request?");
        }
    }

    @GetMapping("/instruments")
    public ResponseEntity<List<String>> getAvailableInstruments() {
        List<String> instrumentIds = instrumentService.getAllInstrumentIds();
        return ResponseEntity.ok(instrumentIds);
    }

    @PostMapping("/approval-request")
    public ResponseEntity<ApprovalRequest> createApprovalRequest(@RequestBody ApprovalRequest request) {
        return ResponseEntity.ok(approvalService.createApprovalRequest(request));
    }

    @GetMapping("/limit/{counterparty}")
    public ResponseEntity<Double> getAvailableLimit(@PathVariable String counterparty) {
        return ResponseEntity.ok(limitService.getAvailableLimit(counterparty));
    }

    @PostMapping("/trade")
    public ResponseEntity<String> executeTrade(@RequestBody TradeRequest tradeRequest) {
        String result = tradeService.processTrade(tradeRequest);
        return ResponseEntity.ok(result);
    }
}