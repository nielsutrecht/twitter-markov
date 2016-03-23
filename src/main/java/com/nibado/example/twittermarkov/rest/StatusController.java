package com.nibado.example.twittermarkov.rest;

import com.nibado.example.twittermarkov.ApplicationStatus;
import com.nibado.example.twittermarkov.markov.MarkovChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import twitter4j.Status;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
public class StatusController {
    private final static Logger LOG = LoggerFactory.getLogger(StatusController.class);
    @Autowired
    private ApplicationStatus applicationStatus;

    @Autowired
    private MarkovChain markovChain;

    @RequestMapping("/applicationStatus")
    public StatusResponse status() {
        StatusResponse response = new StatusResponse();
        response.count = applicationStatus.getStatusCount();

        LOG.debug("ApplicationStatus(count={})", response.count);
        return response;
    }

    @RequestMapping("/markovStatus")
    public MarkovResponse markovStatus() {
        MarkovResponse response = new MarkovResponse();

        response.count = markovChain.getNodeMap().size();
        response.nodes = mapNodes(markovChain);
        response.startCount = markovChain.getStartNode().getCount();
        response.startLinks = mapLinks(markovChain.getStartNode().getLinkMap().values());

        LOG.debug("MarkovStatus(count={},startLinks={})", response.count, response.startCount);

        return response;
    }

    private static List<Node> mapNodes(MarkovChain chain) {
        return chain.getNodeMap().values()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
                .limit(100)
                .map(StatusController::mapNode)
                .collect(Collectors.toList());
    }

    private static List<NodeLink> mapLinks(Collection<MarkovChain.Link> links) {
        return links.stream()
                .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
                .map(l -> new NodeLink(l.getNode().getWord(), l.getCount()))
                .collect(Collectors.toList());
    }

    private static Node mapNode(MarkovChain.Node mNode) {
        Node node = new Node(mNode.getWord(), mNode.getCount());

        return node;
    }

    @RequestMapping("/latestTweet")
    public Status latestTweet() {
        return applicationStatus.getLastStatus();
    }

    @RequestMapping("/random")
    public RandomResponse random() {
        RandomResponse response = new RandomResponse();

        response.text = markovChain.generate();

        return response;
    }

    public static class StatusResponse {
        public long count;
    }

    public static class MarkovResponse {
        public long count;
        public long startCount;
        public List<Node> nodes;
        public List<NodeLink> startLinks;
    }

    public static class Node {
        public String word;
        public int count;
        public List<NodeLink> links;

        private Node(String word, int count) {
            this.word = word;
            this.count = count;
        }
    }

    public static class NodeLink {
        public String word;
        public int count;

        public NodeLink(String word, int count) {
            this.word = word;
            this.count = count;
        }
    }

    public static class RandomResponse {
        public String text;
    }
}
