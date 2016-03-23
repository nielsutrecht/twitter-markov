package com.nibado.example.twittermarkov.twitter;

import com.nibado.example.twittermarkov.ApplicationStatus;
import com.nibado.example.twittermarkov.markov.MarkovChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicLong;

/**
 * StatusListener asynchronously listens to twitter updates.
 */

@Component
public class StatusListener implements twitter4j.StatusListener {
    private static final Logger LOG = LoggerFactory.getLogger(StatusListener.class);
    @Autowired
    private ApplicationStatus applicationStatus;

    @Autowired
    private MarkovChain markovChain;

    @Autowired
    private TwitterStreamFactory twitterStreamFactory;

    @Override
    public void onStatus(Status status) {
        LOG.trace("Tweet: {}", status.getText());

        applicationStatus.incrementStatusCount();
        applicationStatus.updateLastStatus(status);
        if(!status.isRetweet()) {
            markovChain.add(status.getText());
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    @Override
    public void onTrackLimitationNotice(int i) {
    }

    @Override
    public void onScrubGeo(long l, long l1) {
    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {
    }

    @Override
    public void onException(Exception e) {
    }

    @PostConstruct
    public void startListening() {
        LOG.info("Starting TwitterStream");
        TwitterStream twitterStream = twitterStreamFactory.getInstance();

        twitterStream.addListener(this);
        twitterStream.sample("en");
    }
}
