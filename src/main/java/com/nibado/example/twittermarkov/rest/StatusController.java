package com.nibado.example.twittermarkov.rest;

import com.nibado.example.twittermarkov.ApplicationStatus;
import com.nibado.example.twittermarkov.markov.MarkovChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import twitter4j.Status;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class StatusController {
    private AtomicLong counter = new AtomicLong();

    @Autowired
    private ApplicationStatus applicationStatus;

    @Autowired
    private MarkovChain markovChain;

    @RequestMapping("/applicationStatus")
    public StatusResponse status() {
        StatusResponse response = new StatusResponse();
        applicationStatus.incrementStatusCount();
        response.count = applicationStatus.getStatusCount();

        return response;
    }

    @RequestMapping("/latestTweet")
    public Status latestTweet() {
        return applicationStatus.getLastStatus();
    }

    @RequestMapping("/random")
    public RandomResponse random() {
        RandomResponse response = new RandomResponse();
        response.text = markovChain.generate() + counter.incrementAndGet();

        return response;
    }

    public static class StatusResponse {
        public long count;
    }

    public static class RandomResponse {
        public String text;
    }
}
