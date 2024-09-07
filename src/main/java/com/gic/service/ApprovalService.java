package com.gic.service;

import com.gic.model.ApprovalRequest;
import com.gic.model.InstrumentVerificationRequest;
import com.gic.repository.ApprovalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    public ApprovalRequest createApprovalRequest(InstrumentVerificationRequest request) {
        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setInstrumentVerificationRequest(request);
        approvalRequest.setStatus("PENDING");
        return approvalRequestRepository.save(approvalRequest);
    }
}