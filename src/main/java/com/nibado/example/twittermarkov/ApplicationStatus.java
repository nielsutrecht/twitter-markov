package com.nibado.example.twittermarkov;

import twitter4j.Status;

import java.util.concurrent.atomic.AtomicLong;

public class ApplicationStatus {
    private AtomicLong statusCounter = new AtomicLong();
    private Status lastStatus;

    public void incrementStatusCount() {
        statusCounter.incrementAndGet();
    }

    public long getStatusCount() {
        return statusCounter.get();
    }

    public synchronized void updateLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }

    public synchronized Status getLastStatus() {
        return lastStatus;
    }
}
