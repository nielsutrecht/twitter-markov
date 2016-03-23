package com.nibado.example.twittermarkov.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class StatusController {
    private AtomicLong counter = new AtomicLong();
    @RequestMapping("/status")
    public Status status() {
        Status status = new Status();
        status.count = counter.incrementAndGet();

        return status;
    }

    public static class Status {
        public long count;
    }
}
