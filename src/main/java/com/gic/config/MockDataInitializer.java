package com.gic.config;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.gic.model.Instrument;
import com.gic.model.Limit;
import com.gic.repository.InstrumentRepository;
import com.gic.repository.LimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class MockDataInitializer {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private LimitRepository limitRepository;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            loadInstruments();
            loadLimits();
        };
    }

    private void loadInstruments() throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource("instruments.csv").getInputStream()))) {
            List<String[]> rows = reader.readAll();
            rows.remove(0); // Remove header row

            for (String[] row : rows) {
                Instrument instrument = new Instrument();
                instrument.setInstrumentGroup(row[0]);
                instrument.setInstrument(row[1]);
                instrument.setDepartment(row[2]);
                instrument.setRiskCountry(row[3]);
                instrument.setExchange(row[4]);
                instrument.setTradeCCY(row[5]);
                instrument.setSettlementCCY(row[6]);
                instrumentRepository.save(instrument);
            }
        }
    }

    private void loadLimits() throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource("limit.csv").getInputStream()))) {
            List<String[]> rows = reader.readAll();
            rows.remove(0); // Remove header row

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy");

            for (String[] row : rows) {
                Limit limit = new Limit();
                limit.setInstrumentGroup(row[0]);
                limit.setCounterparty(row[1]);
                limit.setCurrency(row[2]);
                limit.setAvailableLimit(Double.parseDouble(row[3].replace(",", "")));
                limit.setDataDate(LocalDate.parse(row[4], formatter));
                limitRepository.save(limit);
            }
        }
    }
}