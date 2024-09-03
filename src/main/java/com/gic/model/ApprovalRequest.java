package com.gic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "approvalRequests")
public class ApprovalRequest {
    @Id
    private String id;
    private String instrumentId;
    private String status;
}