package ua.findvacancies.utils;

import org.springframework.util.CollectionUtils;
import ua.findvacancies.model.Provider;
import ua.findvacancies.model.strategy.Strategy;
import ua.findvacancies.model.viewdata.ViewSearchParams;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ViewSearchParamsUtils {

    public static Set<Provider> getProvidersSet(ViewSearchParams viewSearchParams) {
        if (isEmptyViewSearchParams(viewSearchParams)) {
            return Collections.emptySet();
        }
        Set<Provider> providers = viewSearchParams.getSites().stream()
                .filter(siteName -> Provider.contains(siteName))
                .map(siteName -> Provider.valueOf(siteName.toUpperCase()))
                .collect(Collectors.toSet());
        return providers;
    }

    public static Set<Strategy> getStrategiesSet(ViewSearchParams viewSearchParams) {
        if (isEmptyViewSearchParams(viewSearchParams)) {
            return Collections.emptySet();
        }
        Set<Strategy> strategies = viewSearchParams.getSites().stream()
                .filter(siteName -> Provider.contains(siteName))
                .map(siteName -> Provider.valueOf(siteName.toUpperCase()).getStrategy())
                .collect(Collectors.toSet());
        return strategies;
    }

    private static boolean isEmptyViewSearchParams(ViewSearchParams viewSearchParams) {
        return viewSearchParams == null || CollectionUtils.isEmpty(viewSearchParams.getSites());
    }
}
