package com.nibado.example.twittermarkov.rest;

import com.nibado.example.twittermarkov.ApplicationStatus;
import com.nibado.example.twittermarkov.markov.MarkovChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GraphController {
    private final static Logger LOG = LoggerFactory.getLogger(GraphController.class);

    @Autowired
    private MarkovChain markovChain;

    @RequestMapping("/graphdata")
    public D3Graph graph() {
        D3Graph graph = new D3Graph();

        graph.nodes = new ArrayList<>(markovChain.getNodeMap().size());
        graph.links = new ArrayList<>();

        Map<MarkovChain.Node, Integer> indices = new HashMap<>();
        int index = 0;

        graph.nodes.add(new D3Node("[Start]", 1));
        indices.put(markovChain.getStartNode(), index++);
        graph.nodes.add(new D3Node("[End]", 1));
        indices.put(markovChain.getEndNode(), index++);

        List<MarkovChain.Node> filteredNodes = markovChain.getNodeMap()
                .values()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
                .limit(100)
                .collect(Collectors.toList());

        for(MarkovChain.Node node : filteredNodes) {
            int group = 2;
            if(node.getWord().startsWith("#")) {
                group = 3;
            }
            else if(node.getWord().startsWith("@")) {
                group = 4;
            }
            graph.nodes.add(new D3Node(node.getWord(), group));
            indices.put(node, index++);

        }

        addLinks(markovChain.getStartNode(), indices, graph);
        addLinks(markovChain.getEndNode(), indices, graph);

        for(MarkovChain.Node node : filteredNodes) {
            addLinks(node, indices, graph);
        }

        return graph;
    }

    private static void addLinks(MarkovChain.Node node, Map<MarkovChain.Node, Integer> indices, D3Graph graph) {
        int source = indices.get(node);
        for(MarkovChain.Link link : node.getLinkMap().values()) {
            if(indices.containsKey(link.getNode())) {
                int target = indices.get(link.getNode());
                int value = (int) ((double) link.getCount() / (double) node.getCount() * 100.0);

                graph.links.add(new D3Link(source, target, value));
            }
        }
    }

    public static class D3Graph {
        public List<D3Node> nodes;
        public List<D3Link> links;
    }

    public static class D3Node {
        public final String name;
        public final int group;

        public D3Node(String name, int group) {
            this.name = name;
            this.group = group;
        }
    }

    public static class D3Link {
        public final int source;
        public final int target;
        public final int value;

        public D3Link(int source, int target, int value) {
            this.source = source;
            this.target = target;
            this.value = value;
        }
    }
}
