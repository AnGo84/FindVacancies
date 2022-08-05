package ua.findvacancies.model;

import org.junit.jupiter.api.Test;
import ua.findvacancies.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

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