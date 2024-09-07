package com.gic.service;

import com.gic.model.Limit;
import com.gic.repository.LimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LimitService {

    @Autowired
    private LimitRepository limitRepository;

    public Limit getAvailableLimit(String counterparty, String instrumentGroup) {
        List<Limit> limits = limitRepository.findByCounterpartyAndInstrumentGroup(counterparty, instrumentGroup);
        if (limits.isEmpty()) {
            return null;
        }
        // You might want to add some logic here to handle multiple limits if needed
        return limits.get(0);

        // Option 3: Use this instead if you want to get only the first result
        // return limitRepository.findFirstByCounterpartyAndInstrumentGroup(counterparty, instrumentGroup);
    }

    @Transactional
    public boolean checkAndReserveLimit(String counterparty, String instrumentGroup, Double amount) {
        List<Limit> limits = limitRepository.findByCounterpartyAndInstrumentGroup(counterparty, instrumentGroup);
        if (limits.isEmpty()) {
            return false;
        }
        Limit limit = limits.get(0); // You might want to add some logic here to handle multiple limits if needed
        if (limit.getAvailableLimit() >= amount) {
            limit.setAvailableLimit(limit.getAvailableLimit() - amount);
            limitRepository.save(limit);
            return true;
        }
        return false;
    }

    @Transactional
    public void confirmLimitUsage(String counterparty, String instrumentGroup, Double amount) {
        // The limit has already been updated in checkAndReserveLimit, so we don't need to do anything here
    }

    @Transactional
    public void releaseLimitReservation(String counterparty, String instrumentGroup, Double amount) {
        List<Limit> limits = limitRepository.findByCounterpartyAndInstrumentGroup(counterparty, instrumentGroup);
        if (!limits.isEmpty()) {
            Limit limit = limits.get(0); // You might want to add some logic here to handle multiple limits if needed
            limit.setAvailableLimit(limit.getAvailableLimit() + amount);
            limitRepository.save(limit);
        }
    }
}