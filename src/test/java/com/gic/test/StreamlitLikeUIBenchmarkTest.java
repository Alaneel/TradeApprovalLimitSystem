package com.gic.test;

import com.gic.TradingSystemApplication;
import com.gic.model.Instrument;
import com.gic.model.TradeRequest;
import com.gic.repository.InstrumentRepository;
import com.gic.service.InstrumentService;
import com.gic.service.LimitService;
import com.gic.service.TradeService;
import com.gic.util.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TradingSystemApplication.class)
@ActiveProfiles("test")
public class StreamlitLikeUIBenchmarkTest {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    private LimitService limitService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private DataGenerator dataGenerator;

    private static final int NUM_INSTRUMENTS = 1_000_000; // 1 million instruments
    private static final int NUM_USERS = 100; // Simulate 100 concurrent users
    private static final int TEST_DURATION_MINUTES = 5;
    private static final String[] COUNTERPARTIES = {"Bank A", "Hedge Fund B", "Investment Firm C", "Trader D", "Fund E"};

    private List<String> instrumentIds;
    private Random random = new Random();

    @BeforeEach
    public void setup() {
        instrumentRepository.deleteAll();
        dataGenerator.generateInstruments(NUM_INSTRUMENTS);
        instrumentIds = new ArrayList<>(instrumentService.getAllInstrumentIds());
    }

    @Test
    public void testStreamlitLikeUIInteractions() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_USERS);

        for (int i = 0; i < NUM_USERS; i++) {
            executorService.submit(this::simulateUserSession);
        }

        executorService.shutdown();
        executorService.awaitTermination(TEST_DURATION_MINUTES + 1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Streamlit-like UI Benchmark Results:");
        System.out.println("Number of simulated users: " + NUM_USERS);
        System.out.println("Test duration: " + TEST_DURATION_MINUTES + " minutes");
        System.out.println("Total test time: " + duration + " ms");
    }

    private void simulateUserSession() {
        long sessionStart = System.currentTimeMillis();
        int actionsPerformed = 0;

        while (System.currentTimeMillis() - sessionStart < TimeUnit.MINUTES.toMillis(TEST_DURATION_MINUTES)) {
            try {
                // Simulate user think time
                Thread.sleep(random.nextInt(5000) + 1000);

                // Randomly choose an action
                int action = random.nextInt(4);
                switch (action) {
                    case 0:
                        searchInstrument();
                        break;
                    case 1:
                        checkLimit();
                        break;
                    case 2:
                        executeTrade();
                        break;
                    case 3:
                        browseRecentTrades();
                        break;
                }

                actionsPerformed++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("User session completed. Actions performed: " + actionsPerformed);
    }

    private void searchInstrument() {
        String randomId = instrumentIds.get(random.nextInt(instrumentIds.size()));
        Optional<Instrument> instrument = instrumentService.findInstrument(randomId);
        assertNotNull(instrument, "Instrument search result should not be null");
    }

    private void checkLimit() {
        String counterparty = COUNTERPARTIES[random.nextInt(COUNTERPARTIES.length)];
        Double limit = limitService.getAvailableLimit(counterparty);
        assertNotNull(limit, "Limit should not be null");
    }

    private void executeTrade() {
        String instrumentId = instrumentIds.get(random.nextInt(instrumentIds.size()));
        String counterparty = COUNTERPARTIES[random.nextInt(COUNTERPARTIES.length)];
        double amount = random.nextDouble() * 10000; // Random amount up to 10,000
        TradeRequest tradeRequest = new TradeRequest(instrumentId, counterparty, amount, true);
        String result = tradeService.processTrade(tradeRequest);
        assertNotNull(result, "Trade execution result should not be null");
    }

    private void browseRecentTrades() {
        // Simulate fetching recent trades
        // In a real scenario, you would have a method in TradeService to fetch recent trades
        // For this simulation, we'll just wait for a short time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}