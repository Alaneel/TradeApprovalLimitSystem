package com.gic.util;

import com.gic.model.Instrument;
import com.gic.repository.InstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class DataGenerator {

    @Autowired
    private InstrumentRepository instrumentRepository;

    private static final int BATCH_SIZE = 10000;
    private static final String[] INSTRUMENT_TYPES = {"Stock", "Bond", "ETF", "Future", "Option", "Forex"};

    public void generateInstruments(int numberOfInstruments) {
        Random random = new Random();
        List<Instrument> batch = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < numberOfInstruments; i++) {
            Instrument instrument = new Instrument();
            instrument.setId(UUID.randomUUID().toString());
            instrument.setName("Instrument-" + i);
            instrument.setType(INSTRUMENT_TYPES[random.nextInt(INSTRUMENT_TYPES.length)]);
            // Add more fields as needed to increase data size

            batch.add(instrument);

            if (batch.size() >= BATCH_SIZE) {
                instrumentRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            instrumentRepository.saveAll(batch);
        }
    }
}