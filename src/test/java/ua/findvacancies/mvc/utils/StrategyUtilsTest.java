package ua.findvacancies.mvc.utils;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class StrategyUtilsTest {

    @Test
    public void getKeyWordsLine(){
        Set<String> expectSet = Stream.of("word1", "word2", "word3")
                .collect(Collectors.toCollection(HashSet::new));
        assertEquals(expectSet, StrategyUtils.getKeyWordsSet("word1 word3 word2    "));

        expectSet = Stream.of("word")
                .collect(Collectors.toCollection(HashSet::new));
        assertEquals(expectSet, StrategyUtils.getKeyWordsSet("       word   "));

        Set<String> resultSet = StrategyUtils.getKeyWordsSet(null);
        assertNotNull(resultSet);
        assertTrue(resultSet.isEmpty());

        resultSet = StrategyUtils.getKeyWordsSet("");
        assertNotNull(resultSet);
        assertTrue(resultSet.isEmpty());
    }

    @Test
    public void getExcludeWordsSet() {
        Set<String> resultSet = StrategyUtils.getExcludeWordsSet(null);
        assertNotNull(resultSet);
        assertTrue(resultSet.isEmpty());

        resultSet = StrategyUtils.getExcludeWordsSet("");
        assertNotNull(resultSet);
        assertTrue(resultSet.isEmpty());

        Set<String> set = Stream.of("a", "b", "c")
                .collect(Collectors.toCollection(HashSet::new));
        assertEquals(set, StrategyUtils.getExcludeWordsSet("-a -c -b "));

        set = Stream.of("a", "b", "c")
                .collect(Collectors.toCollection(HashSet::new));
        assertEquals(set, StrategyUtils.getExcludeWordsSet("1 -a d -c e -b ee d-2"));

        assertTrue(StrategyUtils.getExcludeWordsSet("a c b ").isEmpty());
    }
}