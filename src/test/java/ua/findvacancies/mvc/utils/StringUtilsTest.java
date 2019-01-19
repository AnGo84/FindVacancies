package ua.findvacancies.mvc.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by AnGo on 28.07.2017.
 */
public class StringUtilsTest {
    @Rule
    public TestName name = new TestName();


    @Before
    public void setUp() throws Exception {
        System.out.println("Test start " + name.getMethodName());
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Test end " + name.getMethodName());
    }

    @Test
    public void getKeyWordsLine() throws Exception {
        assertEquals("Java+developer", StringUtils.getKeyWordsLine("Java developer -senior","+"));
        assertEquals("", StringUtils.getKeyWordsLine(null,""));
    }

    @Test
    public void getExcludeWordsSet() throws Exception {

    }

    @Test
    public void isStringIncludeWords() throws Exception {
        final String line = "Java developer";
        Set<String> set = new HashSet();
        set.add("java");
        assertTrue(StringUtils.isStringIncludeWords(line, set));

        set.clear();
        set.add("senior");
        assertFalse(StringUtils.isStringIncludeWords(line, set));
    }

    @Test
    public void getDaysFromText() throws Exception {
        assertEquals(2, StringUtils.getDaysFromText("2"));
        assertEquals(0, StringUtils.getDaysFromText(null));
        assertEquals(0, StringUtils.getDaysFromText(""));

    }

    @Test(expected = NumberFormatException.class)
    public void getDaysFromTextWrongParam() throws Exception {
        StringUtils.getDaysFromText("dasdsdad");
    }

}