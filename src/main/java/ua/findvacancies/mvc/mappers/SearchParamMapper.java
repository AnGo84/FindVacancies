package ua.findvacancies.mvc.mappers;

import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.viewdata.ViewSearchParams;
import ua.findvacancies.mvc.utils.AppStringUtils;
import ua.findvacancies.mvc.utils.StrategyUtils;

public class SearchParamMapper implements ObjectMapper<ViewSearchParams, SearchParam> {

    @Override
    public SearchParam convert(ViewSearchParams viewSearchParams) {
        if (viewSearchParams == null) {
            return null;
        }
        SearchParam searchParam = SearchParam.builder()
                .searchLine(viewSearchParams.getSearchLine())
                .days(AppStringUtils.getNumberFromText(viewSearchParams.getDays()))
                .keyWords(StrategyUtils.getKeyWordsSet(viewSearchParams.getSearchLine()))
                .excludeWords(StrategyUtils.getExcludeWordsSet(viewSearchParams.getSearchLine()))
                .build();
        return searchParam;
    }
}
