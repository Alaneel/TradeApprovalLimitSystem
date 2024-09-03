package com.gic.util;

import com.gic.service.InstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class QuerySimulator {

    @Autowired
    private InstrumentService instrumentService;

    private final Random random = new Random();

    public void simulateHeavyQueries(int queriesPerMinute, int durationMinutes) throws InterruptedException {
        int totalQueries = queriesPerMinute * durationMinutes;
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<String> instrumentIds = new ArrayList<>(instrumentService.getAllInstrumentIds());

        for (int i = 0; i < totalQueries; i++) {
            executorService.submit(() -> {
                String randomId = instrumentIds.get(random.nextInt(instrumentIds.size()));
                instrumentService.findInstrument(randomId);
            });

            if (i % queriesPerMinute == 0) {
                Thread.sleep(60000); // Wait for a minute
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }
}