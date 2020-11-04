package ua.findvacancies.mvc.model;

import org.junit.Test;
import ua.findvacancies.mvc.TestUtils;

import static org.junit.Assert.assertEquals;

public class SearchParamTest {
    private SearchParam searchParam;

    @Test
    public void whenSearchParamReturnResult() {
        searchParam = new SearchParam();
        assertEquals(SearchParam.EMPTY_SEARCH_LINE, searchParam.getKeyWordsSearchLine());
        assertEquals(SearchParam.EMPTY_SEARCH_LINE, searchParam.getKeyWordsSearchLine(null));
        assertEquals(SearchParam.EMPTY_SEARCH_LINE, searchParam.getKeyWordsSearchLine(""));
        assertEquals(SearchParam.EMPTY_SEARCH_LINE, searchParam.getKeyWordsSearchLine("-"));
        searchParam = TestUtils.getSearchParams();
        assertEquals("search+line", searchParam.getKeyWordsSearchLine());
        assertEquals("search+line", searchParam.getKeyWordsSearchLine(null));
        assertEquals("search+line", searchParam.getKeyWordsSearchLine(""));
        assertEquals("search-line", searchParam.getKeyWordsSearchLine("-"));
    }

}