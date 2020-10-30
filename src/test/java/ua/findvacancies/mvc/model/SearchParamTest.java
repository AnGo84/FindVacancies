package ua.findvacancies.mvc.model;

import org.junit.Test;
import ua.findvacancies.mvc.TestUtils;

import static org.junit.Assert.assertEquals;

public class SearchParamTest {
    private SearchParam searchParam;

    @Test
    public void whenSearchParamReturnResult() {
        searchParam = TestUtils.getSearchParams();
        assertEquals("search+line", searchParam.getKeyWordsSearchLine());
        assertEquals("search+line", searchParam.getKeyWordsSearchLine(null));
        assertEquals("search+line", searchParam.getKeyWordsSearchLine(""));
        assertEquals("search-line", searchParam.getKeyWordsSearchLine("-"));
    }

}