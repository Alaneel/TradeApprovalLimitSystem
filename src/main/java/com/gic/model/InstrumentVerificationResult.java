package com.gic.model;

import lombok.Data;

@Data
public class InstrumentVerificationResult {
    private boolean valid;
    private boolean approvedForDepartment;
    private Instrument instrument;
    private String message;

    public InstrumentVerificationResult() {
        this.valid = false;
        this.approvedForDepartment = false;
        this.instrument = null;
        this.message = "";
    }
}