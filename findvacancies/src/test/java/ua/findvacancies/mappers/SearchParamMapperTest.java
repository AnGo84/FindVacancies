package ua.findvacancies.mappers;


import org.junit.jupiter.api.Test;
import ua.findvacancies.TestUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.viewdata.ViewSearchParams;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SearchParamMapperTest {

    private final SearchParamMapper objectMapper = new SearchParamMapper();

    @Test
    public void whenConvertReturnResult() {
        String searchLine = "Search Line -with -exclude";
        ViewSearchParams viewSearchParams = TestUtils.getViewSearchParams();
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
        SearchParam searchParam = SearchParam.builder()
                .searchLine("Search Line -with -exclude")
                .days(5)
                .keyWords(Stream.of("search", "line")
                        .collect(Collectors.toCollection(HashSet::new)))
                .excludeWords(Stream.of("with", "exclude")
                        .collect(Collectors.toCollection(HashSet::new)))
                .build();
        return searchParam;
    }
}