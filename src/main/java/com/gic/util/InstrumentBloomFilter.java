package com.gic.util;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
public class InstrumentBloomFilter {

    private BloomFilter<String> bloomFilter;

    public InstrumentBloomFilter() {
        // Initialize with expected number of instruments and desired false positive probability
        this.bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 1000000, 0.01);
    }

    public void add(String instrumentId) {
        bloomFilter.put(instrumentId);
    }

    public boolean mightContain(String instrumentId) {
        return bloomFilter.mightContain(instrumentId);
    }

    public void clear() {
        this.bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 1000000, 0.01);
    }
}