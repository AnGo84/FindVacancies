package ua.findvacancies.mappers;

import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.utils.AppStringUtils;
import ua.findvacancies.utils.StrategyUtils;

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
