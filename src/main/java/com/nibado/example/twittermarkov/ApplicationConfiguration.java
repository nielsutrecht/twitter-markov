package com.nibado.example.twittermarkov;

import com.nibado.example.twittermarkov.markov.MarkovChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Spring configuration class that reads settings from the configuration.properties file and constructs beans from them.
 *
 * WARNING: .gitignore the configuration.properties file since it contains your OAuth secrets!
 */
@Configuration
@PropertySource("classpath:configuration.properties")
public class ApplicationConfiguration {
    @Value("${twitter4j.oauth.consumerKey}") String t4jConsumerKey;
    @Value("${twitter4j.oauth.consumerSecret}") String t4jConsumerSecret;
    @Value("${twitter4j.oauth.accessToken}") String t4jAccessToken;
    @Value("${twitter4j.oauth.accessTokenSecret}") String t4jAccessTokenSecret;

    @Bean
    public ApplicationStatus getStatus() {
        return new ApplicationStatus();
    }

    @Bean
    public MarkovChain getMarkovChain() {
        return new MarkovChain();
    }

    @Bean
    public TwitterStreamFactory getTwitterStreamFactory() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(t4jConsumerKey)
                .setOAuthConsumerSecret(t4jConsumerSecret)
                .setOAuthAccessToken(t4jAccessToken)
                .setOAuthAccessTokenSecret(t4jAccessTokenSecret);
        return new TwitterStreamFactory(cb.build());
    }
}
