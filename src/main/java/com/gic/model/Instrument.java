package com.gic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "instruments")
public class Instrument {
    @Id
    private String id;
    private String name;
    private String type;
}