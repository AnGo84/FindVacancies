package ua.findvacancies.mvc.mappers;

import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.utils.AppStringUtils;
import ua.findvacancies.mvc.utils.StrategyUtils;
import ua.findvacancies.mvc.viewdata.ViewSearchParams;

public class SearchParamMapper implements ObjectMapper<ViewSearchParams, SearchParam> {

    @Override
    public SearchParam convert(ViewSearchParams viewSearchParams) {
        if (viewSearchParams == null) {
            return null;
        }
        SearchParam searchParam = new SearchParam();
        searchParam.setSearchLine(viewSearchParams.getSearchLine());
        searchParam.setDays(-AppStringUtils.getNumberFromText(viewSearchParams.getDays()));
        searchParam.setKeyWords(StrategyUtils.getKeyWordsSet(viewSearchParams.getSearchLine()));
        searchParam.setExcludeWords(StrategyUtils.getExcludeWordsSet(viewSearchParams.getSearchLine()));
        return searchParam;
    }
}
