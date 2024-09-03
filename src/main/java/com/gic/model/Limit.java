package com.gic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "limits")
public class Limit {
    @Id
    private String id;
    private String counterparty;
    private Double totalLimit;
    private Double usedLimit;
}