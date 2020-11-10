package ua.findvacancies.mvc.utils;

import org.springframework.util.CollectionUtils;
import ua.findvacancies.mvc.model.Provider;
import ua.findvacancies.mvc.model.strategy.Strategy;
import ua.findvacancies.mvc.model.viewdata.ViewSearchParams;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ViewSearchParamsUtils {

    public static Set<Provider> getProvidersSet(ViewSearchParams viewSearchParams) {
        if (viewSearchParams == null || CollectionUtils.isEmpty(viewSearchParams.getSites())) {
            return Collections.emptySet();
        }
        Set<Provider> providers = viewSearchParams.getSites().stream()
                .filter(siteName -> Provider.contains(siteName))
                .map(siteName -> Provider.valueOf(siteName.toUpperCase()))
                .collect(Collectors.toSet());
        return providers;
    }

    public static Set<Strategy> getStrategiesSet(ViewSearchParams viewSearchParams) {
        if (viewSearchParams == null || CollectionUtils.isEmpty(viewSearchParams.getSites())) {
            return Collections.emptySet();
        }
        Set<Strategy> strategies = viewSearchParams.getSites().stream()
                .filter(siteName -> Provider.contains(siteName))
                .map(siteName -> Provider.valueOf(siteName.toUpperCase()).getStrategy())
                .collect(Collectors.toSet());
        return strategies;
    }
}
