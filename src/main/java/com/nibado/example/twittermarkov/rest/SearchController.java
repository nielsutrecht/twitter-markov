package com.nibado.example.twittermarkov.rest;

import com.nibado.example.twittermarkov.markov.MarkovChain;
import com.nibado.example.twittermarkov.twitter.StatusSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private StatusSearch statusSearch;

    @RequestMapping(value = "/timeline/{user}", method = RequestMethod.GET)
    public RandomResponse timeline(@PathVariable String user) throws Exception {
        MarkovChain chain = statusSearch.fromTimeline(user);
        return new RandomResponse(chain.generate());
    }

    @RequestMapping(value = "/query/{query}", method = RequestMethod.GET)
    public RandomResponse query(@PathVariable String query) throws Exception  {
        return new RandomResponse("query:" + query);
    }
}
