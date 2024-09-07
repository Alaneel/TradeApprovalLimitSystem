package com.gic.model;

import lombok.Data;

@Data
public class InstrumentVerificationRequest {
    private String instrumentGroup;
    private String instrument;
    private String settlementCurrency;
    private String tradeCurrency;
    private String country;
    private String exchange;
    private String department;
}