package com.nibado.example.twittermarkov.markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkovChain {
    private final Node startNode = new Node(null);
    private final Node endNode = new Node(null);

    private Map<String, Node> nodeMap = new HashMap<>();

    public void add(String text) {
        List<WordPair> pairs = toPairs(text);

        for(WordPair pair : pairs) {
            Node from;
            Node to;
            if(pair.isFirst()) {
                from = startNode;
                to = getNode(pair.to);
            }
            else if(pair.isLast()) {
                from = getNode(pair.from);
                to = endNode;
            }
            else {
                from = getNode(pair.from);
                to = getNode(pair.to);
            }

            from.add(to);
        }
    }

    public Node getNode(String word) {
        if(!nodeMap.containsKey(word)) {
            nodeMap.put(word, new Node(word));
        }

        return nodeMap.get(word);
    }

    public String generate() {
        return "Test tweet";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        toString(startNode, builder);
        for(Node n : nodeMap.values()) {
            toString(n, builder);
        }

        return builder.toString();
    }

    private void toString(Node node, StringBuilder builder) {
        builder.append(String.format("%s (%s) =>\n", node.getWord(), node.count));
        for(Link link : node.linkMap.values()) {
            double ratio = (double)link.count / (double)node.count;
            builder.append(String.format("          %s (%s = %.4f)\n", link.node.getWord(), link.count, ratio));
        }
    }

    public static List<WordPair> toPairs(String text) {
        String[] parts = text.split("\\s+");

        List<WordPair> pairs = new ArrayList<>(parts.length + 1);

        for(int i = 0;i < parts.length - 1;i++) {
            if(i == 0) {
                pairs.add(new WordPair(null, parts[0]));
            }
            pairs.add(new WordPair(parts[i], parts[i + 1]));
            if(i == parts.length - 2) {
                pairs.add(new WordPair(parts[i + 1], null));
            }
        }

        return pairs;
    }

    public class Node {
        private String word;
        private int count;
        private Map<Node, Link> linkMap = new HashMap<>();

        public Node(String word) {
            this.word = word;
            this.count = 0;
        }

        @Override
        public boolean equals(Object other) {
            if(!(other instanceof Node)) {
                return false;
            }

            if(this == startNode && other == startNode) {
                return true;
            }
            else if(this == endNode && other == endNode) {
                return true;
            }
            else {
                return word.equals(((Node)other).word);
            }
        }

        @Override
        public int hashCode() {
            if(this == startNode) {
                return 0xFFFFFFFF;
            }
            else if(this == endNode) {
                return 0x7FFFFF;
            }
            else {
                return word.hashCode();
            }
        }

        public String getWord() {
            if(this == startNode) {
                return "[S]";
            }
            else if(this == endNode) {
                return "[E]";
            }
            else {
                return word;
            }
        }

        private void add(Node to) {
            Link link;
            if(!linkMap.containsKey(to)) {
                link = new Link();
                link.node = to;
                linkMap.put(to, link);
            }
            else {
                link = linkMap.get(to);
            }
            count++;
            link.count++;
        }
    }

    public static class Link {
        private int count;
        private Node node;
    }

    public static class WordPair {
        private String from;
        private String to;

        private WordPair(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public boolean isFirst() {
            return from == null;
        }

        public boolean isLast() {
            return to == null;
        }

        @Override
        public String toString() {
            return String.format("%s>%s", from, to);
        }
    }
}
