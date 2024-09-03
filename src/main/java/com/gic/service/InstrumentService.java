package com.gic.service;

import com.gic.model.Instrument;
import com.gic.repository.InstrumentRepository;
import com.gic.util.InstrumentBloomFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InstrumentService {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private InstrumentBloomFilter bloomFilter;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_HASH_KEY = "instruments";

    @PostConstruct
    @Scheduled(fixedRate = 3600000) // Refresh every hour
    public void initializeBloomFilter() {
        bloomFilter.clear();
        List<String> allInstrumentIds = getAllInstrumentIds();
        allInstrumentIds.forEach(bloomFilter::add);
    }

    @Cacheable(value = "instruments", key = "#id")
    public Optional<Instrument> findInstrument(String id) {
        log.debug("Finding instrument with id: {}", id);

        if (!bloomFilter.mightContain(id)) {
            log.debug("Instrument {} not found in bloom filter", id);
            return Optional.empty();
        }

        log.debug("Checking Redis cache for instrument {}", id);
        Object cachedObject = redisTemplate.opsForHash().get(REDIS_HASH_KEY, id);
        if (cachedObject instanceof Instrument) {
            log.debug("Instrument {} found in Redis cache", id);
            return Optional.of((Instrument) cachedObject);
        }

        log.debug("Instrument {} not found in Redis, checking database", id);
        Optional<Instrument> instrumentOpt = instrumentRepository.findById(id);

        if (instrumentOpt.isPresent()) {
            log.debug("Instrument {} found in database, caching in Redis", id);
            redisTemplate.opsForHash().put(REDIS_HASH_KEY, id, instrumentOpt.get());
        } else {
            log.debug("Instrument {} not found in database", id);
        }

        return instrumentOpt;
    }

    public List<String> getAllInstrumentIds() {
        return instrumentRepository.findAll().stream()
                .map(Instrument::getId)
                .collect(Collectors.toList());
    }
}