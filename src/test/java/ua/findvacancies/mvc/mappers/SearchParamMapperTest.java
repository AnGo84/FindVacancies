package ua.findvacancies.mvc.mappers;

import org.junit.Test;
import ua.findvacancies.mvc.TestUtils;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.viewdata.ViewSearchParams;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SearchParamMapperTest {

    private SearchParamMapper objectMapper = new SearchParamMapper();

    @Test
    public void whenConvertReturnResult() {
        String searchLine = "Search Line -with -exclude";
        ViewSearchParams viewSearchParams =  TestUtils.getViewSearchParams();
        viewSearchParams.setSearchLine(searchLine);
        SearchParam expectedSearchParam = getSearchParams();
        SearchParam searchParam = objectMapper.convert(viewSearchParams);

        assertEquals(expectedSearchParam, searchParam);

        viewSearchParams.setDays("Wrong");
        searchParam = objectMapper.convert(viewSearchParams);
        assertNotEquals(expectedSearchParam, searchParam);
        assertEquals(0, searchParam.getDays());

        viewSearchParams = TestUtils.getViewSearchParams();
        viewSearchParams.setSearchLine("Search Line");

        searchParam = objectMapper.convert(viewSearchParams);
        expectedSearchParam.setExcludeWords(new HashSet<>());
        expectedSearchParam.setSearchLine("Search Line");
        assertEquals(expectedSearchParam, searchParam);
        assertNotNull(searchParam.getExcludeWords());
        assertTrue(searchParam.getExcludeWords().isEmpty());

        viewSearchParams.setSearchLine("-with -exclude");
        expectedSearchParam = getSearchParams();
        expectedSearchParam.setSearchLine("-with -exclude");
        searchParam = objectMapper.convert(viewSearchParams);
        expectedSearchParam.setKeyWords(new HashSet<>());
        assertEquals(expectedSearchParam, searchParam);
        assertNotNull(searchParam.getKeyWords());
        assertTrue(searchParam.getKeyWords().isEmpty());
    }

    @Test
    public void whenConvertReturnNull() {
        assertNull(objectMapper.convert(null));
    }

    private SearchParam getSearchParams() {
        SearchParam searchParam = new SearchParam();
        searchParam.setDays(5);
        searchParam.setSearchLine("Search Line -with -exclude");
        searchParam.setKeyWords(Stream.of("search", "line")
                .collect(Collectors.toCollection(HashSet::new)));
        searchParam.setExcludeWords(Stream.of("with", "exclude")
                .collect(Collectors.toCollection(HashSet::new)));
        return searchParam;
    }
}