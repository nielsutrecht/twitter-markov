package com.nibado.example.twittermarkov.markov;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.nibado.example.twittermarkov.markov.MarkovChain.toPairs;
import static org.assertj.core.api.Assertions.assertThat;

public class MarkovChainTest {
    private MarkovChain chain;
    @Before
    public void setUp() throws Exception {
        chain = new MarkovChain();
    }

    @Test
    public void testToString() {
        chain.add("a b c");
        chain.add("a b c");
        chain.add("b b a");

        System.out.println(chain.toString());

        System.out.println(chain.generate());
        System.out.println(chain.generate());
        System.out.println(chain.generate());
        System.out.println(chain.generate());
    }

    @Test
    public void testToPairs() throws Exception {
        List<MarkovChain.WordPair> pairs = toPairs("a b c");
        assertThat(pairs).hasSize(4);
        assertThat(pairs).extracting(MarkovChain.WordPair::toString).containsExactly("null>a", "a>b", "b>c", "c>null");
    }
}