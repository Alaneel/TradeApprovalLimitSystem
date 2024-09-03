package com.gic.service;

import com.gic.model.Limit;
import com.gic.repository.LimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimitService {

    @Autowired
    private LimitRepository limitRepository;

    public Double getAvailableLimit(String counterparty) {
        Limit limit = limitRepository.findByCounterparty(counterparty);
        return limit != null ? limit.getTotalLimit() - limit.getUsedLimit() : 0.0;
    }

    public boolean checkLimit(String counterparty, Double amount) {
        Limit limit = limitRepository.findByCounterparty(counterparty);
        return limit != null && (limit.getTotalLimit() - limit.getUsedLimit()) >= amount;
    }

    public void updateLimit(String counterparty, Double amount) {
        Limit limit = limitRepository.findByCounterparty(counterparty);
        if (limit != null) {
            limit.setUsedLimit(limit.getUsedLimit() + amount);
            limitRepository.save(limit);
        }
    }
}
