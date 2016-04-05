package com.nibado.example.twittermarkov.twitter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.nibado.example.twittermarkov.markov.MarkovChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.concurrent.TimeUnit;

public class StatusSearch {
    private static final Logger LOG = LoggerFactory.getLogger(StatusSearch.class);

    @Autowired
    private TwitterFactory factory;

    private Cache<String, MarkovChain> queryCache;
    private Cache<String, MarkovChain> timelineCache;

    public StatusSearch() {
        queryCache = CacheBuilder
                .newBuilder()
                .maximumSize(100)
                .removalListener(n -> LOG.debug("Dropped key {} from query cache", n.getKey()))
                .expireAfterAccess(10, TimeUnit.MINUTES).build();

        timelineCache = CacheBuilder
                .newBuilder()
                .maximumSize(100)
                .removalListener(n -> LOG.debug("Dropped key {} from timeline cache", n.getKey()))
                .expireAfterAccess(10, TimeUnit.MINUTES).build();
    }

    public MarkovChain fromQuery(String query) throws TwitterException {
        MarkovChain chain = queryCache.getIfPresent(query);
        if(chain == null) {
            chain = fromQuery(new Query(query));
            queryCache.put(query, chain);
        }
        return chain;
    }

    private MarkovChain fromQuery(Query query) throws TwitterException {
        Twitter tw = factory.getInstance();
        MarkovChain chain = new MarkovChain();
        QueryResult result;
        do {
            result = tw.search(query);
            result.getTweets().stream().map(Status::getText).forEach(chain::add);
        }
        while ((query = result.nextQuery()) != null);

        return chain;
    }

    public MarkovChain fromTimeline(String user) throws TwitterException {
        user = user.toLowerCase();
        MarkovChain chain = timelineCache.getIfPresent(user);
        if(chain == null) {
            Twitter tw = factory.getInstance();
            chain = new MarkovChain();
            tw.getUserTimeline(user)
                    .stream()
                    .map(Status::getText)
                    .forEach(chain::add);
            timelineCache.put(user, chain);
        }

        return chain;
    }
}
