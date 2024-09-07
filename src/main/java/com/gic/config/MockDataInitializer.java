package com.gic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gic.model.Instrument;
import com.gic.model.Limit;
import com.gic.model.ApprovalRequest;
import com.gic.repository.InstrumentRepository;
import com.gic.repository.LimitRepository;
import com.gic.repository.ApprovalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Configuration
public class MockDataInitializer {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    CommandLineRunner initDatabase(InstrumentRepository instrumentRepository,
                                   LimitRepository limitRepository,
                                   ApprovalRequestRepository approvalRequestRepository) {
        return args -> {
            try {
                InputStream inputStream = new ClassPathResource("mockData.json").getInputStream();
                Map<String, List<Map<String, Object>>> mockData = objectMapper.readValue(inputStream, Map.class);

                // Clear existing data
                instrumentRepository.deleteAll();
                limitRepository.deleteAll();
                approvalRequestRepository.deleteAll();

                // Load Instruments
                List<Map<String, Object>> instruments = mockData.get("instruments");
                for (Map<String, Object> instrumentData : instruments) {
                    Instrument instrument = new Instrument();
                    instrument.setId((String) instrumentData.get("id"));
                    instrument.setName((String) instrumentData.get("name"));
                    instrument.setType((String) instrumentData.get("type"));
                    instrumentRepository.save(instrument);
                }

                // Load Limits
                List<Map<String, Object>> limits = mockData.get("limits");
                for (Map<String, Object> limitData : limits) {
                    Limit limit = new Limit();
                    limit.setId((String) limitData.get("id"));
                    limit.setCounterparty((String) limitData.get("counterparty"));
                    limit.setTotalLimit(((Number) limitData.get("totalLimit")).doubleValue());
                    limit.setUsedLimit(((Number) limitData.get("usedLimit")).doubleValue());
                    limitRepository.save(limit);
                }

                // Load Approval Requests
                List<Map<String, Object>> approvalRequests = mockData.get("approvalRequests");
                for (Map<String, Object> requestData : approvalRequests) {
                    ApprovalRequest request = new ApprovalRequest();
                    request.setId((String) requestData.get("id"));
                    request.setInstrumentId((String) requestData.get("instrumentId"));
                    request.setStatus((String) requestData.get("status"));
                    approvalRequestRepository.save(request);
                }

                System.out.println("Mock data loaded successfully.");
            } catch (IOException e) {
                System.err.println("Failed to load mock data: " + e.getMessage());
                System.err.println("The application will start without mock data.");
            }
        };
    }
}