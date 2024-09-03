package com.gic.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "trade_history")
public class TradeHistory {
    @Id
    private String id;
    private String instrumentId;
    private String counterparty;
    private Double amount;
    private LocalDateTime timestamp;
    private String status;
}