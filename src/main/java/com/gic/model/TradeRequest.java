package com.gic.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequest {
    private InstrumentVerificationRequest instrumentVerificationRequest;
    private String counterparty;
    private Double amount;
    private boolean confirmed;
}