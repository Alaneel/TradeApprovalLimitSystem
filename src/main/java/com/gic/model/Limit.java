package com.gic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.LocalDate;

@Data
@Document(collection = "limits")
public class Limit {
    @Id
    private String id;
    private String instrumentGroup;
    private String counterparty;
    private String currency;
    private Double availableLimit;
    private LocalDate dataDate;
}